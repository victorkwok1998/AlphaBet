package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.databinding.DialogLoadingBinding
import com.example.alphabet.databinding.FragmentBacktestStrategyInputBinding
import com.example.alphabet.util.Constants.Companion.sdfISO
import com.example.alphabet.viewmodel.BacktestViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ta4j.core.BarSeriesManager
import org.ta4j.core.BaseBarSeries
import yahoofinance.histquotes.HistoricalQuote

class BacktestStrategyInputFragment: Fragment(), StrategyListAdapter.OnItemClickListener {
    private val viewModel: BacktestViewModel by navGraphViewModels(R.id.nav_graph_backtest)
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var strategyListAdapter: StrategyListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)
        val binding = FragmentBacktestStrategyInputBinding.inflate(inflater, container, false)

        binding.topAppBar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.confirm_button -> {
                    if (viewModel.checkedStrategy.isNotEmpty()) {
                        val action = BacktestStrategyInputFragmentDirections.actionBacktestStrategyInputFragmentToRunStrategyDialog()
                        findNavController().navigate(action)
                    }
                    else {
                        Toast.makeText(requireContext(), "Please select at least one strategy", Toast.LENGTH_LONG).show()
                    }
                    true
                }
                R.id.set_time_period -> {
                    val action = BacktestStrategyInputFragmentDirections.actionGlobalTimePeriodBottomSheetFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }

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

        setStrategyChipGroupFilter(
            chipGroup = binding.strategyList.strategyFilterChipGroup,
            db = databaseViewModel,
            adapter = strategyListAdapter
        )

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