package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.databinding.FragmentSelectStrategyBinding
import com.example.alphabet.viewmodel.BacktestViewModel

class SelectStrategyFragment: Fragment(), StrategyListAdapter.OnItemClickListener {
    private val viewModel: BacktestViewModel by navGraphViewModels(R.id.nav_graph_backtest)
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var strategyListAdapter: StrategyListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)
        val binding = FragmentSelectStrategyBinding.inflate(inflater, container, false)
        strategyListAdapter = StrategyListAdapter(
            requireContext(),
            this,
            enableCheckBox = true,
            checkedStrategy = viewModel.checkedStrategy
        )

        binding.topAppBar.setNavigationOnClickListener { findNavController().popBackStack() }

        val strategyListView = binding.strategyList.myStrategyList
        strategyListView.adapter = strategyListAdapter
        strategyListView.layoutManager = LinearLayoutManager(requireContext())
        strategyListView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        databaseViewModel.readAllStrategy.observe(viewLifecycleOwner) {
            strategyListAdapter.updateList(it)
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