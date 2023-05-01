package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.databinding.FragmentBacktestStrategyInputBinding
import com.example.alphabet.viewmodel.BacktestViewModel

class BacktestStrategyInputFragment: Fragment(), StrategyListAdapter.OnItemClickListener {
    private val viewModel: BacktestViewModel by navGraphViewModels(R.id.nav_graph_backtest)
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var strategyListAdapter: StrategyListAdapter
    private val args: BacktestStrategyInputFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)
        val binding = FragmentBacktestStrategyInputBinding.inflate(inflater, container, false)

        binding.topAppBar.setNavigationOnClickListener { findNavController().popBackStack() }
//        binding.topAppBar.setOnMenuItemClickListener {
//            when(it.itemId) {
//                R.id.confirm_button -> {
//                    if (viewModel.checkedStrategy.isNotEmpty()) {
//                        val action = BacktestStrategyInputFragmentDirections.actionGlobalRunStrategyDialog(
//                            viewModel.start.value!!,
//                            viewModel.end.value!!,
//                            viewModel.checkedStrategy.values.toTypedArray(),
//                            viewModel.stockList.toTypedArray(),
//                            viewModel.transactionCost.value!!
//                        )
//                        findNavController().navigate(action)
//                    }
//                    else {
//                        Toast.makeText(requireContext(), "Please select at least one strategy", Toast.LENGTH_LONG).show()
//                    }
//                    true
//                }
//                R.id.set_time_period -> {
//                    val action = BacktestStrategyInputFragmentDirections.actionBacktestStrategyInputFragmentToBacktestTimePeriodFragment()
//                    findNavController().navigate(action)
//                    true
//                }
//                else -> false
//            }
//        }

        args.strategyList.forEach { viewModel.checkedStrategy[it.id] = it }
        strategyListAdapter = StrategyListAdapter(
            requireContext(),
            this,
            enableCheckBox = true,
            checkedStrategy = viewModel.checkedStrategy
        )
        val strategyListView = binding.strategyList.myStrategyList
        strategyListView.adapter = strategyListAdapter
        strategyListView.layoutManager = LinearLayoutManager(requireContext())
        strategyListView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        databaseViewModel.readAllStrategy.observe(viewLifecycleOwner) {
            strategyListAdapter.updateList(it)
        }
//        binding.viewEmptyStrategy.root.isVisible = strategyListAdapter.getList().isEmpty()

        setStrategyChipGroupFilter(
            chipGroup = binding.strategyList.strategyFilterChipGroup,
            db = databaseViewModel,
            adapter = strategyListAdapter
        )

        binding.buttonRunStrategy.setOnClickListener {
            if (viewModel.checkedStrategy.isNotEmpty()) {
                val action = BacktestStrategyInputFragmentDirections.actionGlobalRunStrategyDialog(
                    viewModel.start.value!!,
                    viewModel.end.value!!,
                    viewModel.checkedStrategy.values.toTypedArray(),
                    viewModel.stockList.toTypedArray(),
                    CostInput(viewModel.transactionCost.value!!.toFloat(), viewModel.transactionCostType.value!!)
                )
                findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "Please select at least one strategy", Toast.LENGTH_LONG).show()
            }
        }

        binding.buttonAdvOpt.setOnClickListener {
            val action = BacktestStrategyInputFragmentDirections.actionBacktestStrategyInputFragmentToBacktestAdvancedOptionsBottomSheet()
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onItemClick(position: Int, checkBox: CheckBox?) {
        val item = strategyListAdapter.getList()[position]
        if (viewModel.checkedStrategy.contains(item.id))
            viewModel.checkedStrategy.remove(item.id)
        else
            viewModel.checkedStrategy[item.id] = item
        checkBox?.apply { this.isChecked = !this.isChecked }
    }
}