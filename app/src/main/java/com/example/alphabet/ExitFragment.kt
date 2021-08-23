package com.example.alphabet

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.databinding.CreateStrategyBinding
import com.example.alphabet.databinding.FragmentEntryBinding
import com.example.alphabet.databinding.FragmentExitBinding
import com.example.alphabet.databinding.SelectIndicatorBinding
import com.google.android.material.card.MaterialCardView

class ExitFragment: Fragment (R.layout.fragment_exit), StrategyAdapter.OnItemClickListener, StrategyAdapter.OnItemLongClickListener  {
    private var _binding: FragmentExitBinding? = null
    private val binding get() = _binding!!
    private lateinit var createStrategyBinding: CreateStrategyBinding
    private val viewModel: StrategyViewModel by navGraphViewModels(R.id.strategy_nav_graph)
    private val strategyAdapter: StrategyAdapter by lazy {
        StrategyAdapter(viewModel.exitRulesInput, this, this)
    }
    private var actionMode: ActionMode? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExitBinding.inflate(inflater, container, false)
        createStrategyBinding = CreateStrategyBinding.bind(binding.root)

        if (viewModel.exitRulesInput.size > 0)
            createStrategyBinding.createStratText.visibility = View.GONE
        else
            createStrategyBinding.createStratText.visibility = View.VISIBLE

        setStratList(context, createStrategyBinding, strategyAdapter)

        createStrategyBinding.stratAppBar.title = resources.getText(R.string.text_view_exit_strat)
        createStrategyBinding.addRuleButton.setOnClickListener {
            val action = ExitFragmentDirections.actionExitFragmentToCreateRuleFragment("Exit")
            findNavController().navigate(action)
        }
        createStrategyBinding.stratAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.action_confirm_strat -> {
                    viewModel.stopGain.value = createStrategyBinding.stopGainText.text.toString().toFloatOrNull()
                    viewModel.stopLoss.value = createStrategyBinding.stopLossText.text.toString().toFloatOrNull()

                    val action = ExitFragmentDirections.actionExitFragmentToBacktestResultFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }

        return binding.root
    }
    override fun onItemClick(position: Int) {
        if (actionMode == null) {
            val action = ExitFragmentDirections.actionExitFragmentToCreateRuleFragment("Exit", position)
            findNavController().navigate(action)
        } else {
            val card = createStrategyBinding.stratList.layoutManager!!.findViewByPosition(position) as MaterialCardView
            card.isChecked = !card.isChecked
        }
    }

    override fun onItemLongClick(position: Int) {
        val card = createStrategyBinding.stratList.layoutManager?.findViewByPosition(position) as MaterialCardView

        when(actionMode) {
            null -> {
                actionMode = activity?.startActionMode(actionModeCallback)
                card.isChecked = !card.isChecked
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val inflater: MenuInflater = mode!!.menuInflater
            inflater.inflate(R.menu.delete_app_bar, menu)
            mode.title = "Delete Rule"
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return strategyActionItemClicked(
                mode,
                item,
                createStrategyBinding,
                viewModel.exitRulesInput,
                strategyAdapter
            )
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
            for (i in 0 until viewModel.exitRulesInput.size) {
                val card = createStrategyBinding.stratList.layoutManager!!.findViewByPosition(i) as MaterialCardView
                card.isChecked = false
            }
        }
    }
}