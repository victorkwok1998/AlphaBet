package com.example.alphabet

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.databinding.CreateStrategyBinding
import com.example.alphabet.databinding.FragmentEntryBinding
import com.google.android.material.card.MaterialCardView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.ISelectionListener
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.select.selectExtension


class EntryFragment: Fragment(R.layout.fragment_entry),
    StrategyAdapter.OnItemClickListener,
    StrategyAdapter.OnItemLongClickListener {
    private var _binding: FragmentEntryBinding? = null
    private val binding get() = _binding!!
    private lateinit var createStrategyBinding: CreateStrategyBinding
    private val viewModel: StrategyViewModel by navGraphViewModels(R.id.strategy_nav_graph)
    private val strategyAdapter: StrategyAdapter by lazy {
        StrategyAdapter(viewModel.entryRulesInput, this, this)
    }
    private var actionMode: ActionMode? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEntryBinding.inflate(inflater, container, false)
        createStrategyBinding = CreateStrategyBinding.bind(binding.root)
        createStrategyBinding.stopGainLossLayout.visibility = View.GONE
        if (viewModel.entryRulesInput.size > 0)
            createStrategyBinding.createStratText.visibility = View.GONE
        else
            createStrategyBinding.createStratText.visibility = View.VISIBLE

        setStratList(context, createStrategyBinding, strategyAdapter)
//        val itemAdapter = ItemAdapter<BindingRuleItem>()
//            .set(viewModel.entryRulesInput.mapIndexed { i, v ->  BindingRuleItem(v, i, requireContext())})
//        val fastAdapter = FastAdapter.with(itemAdapter)
//
//        fastAdapter.onClickListener = { view, adapter, item, position ->
//            val action = EntryFragmentDirections.actionEntryFragmentToCreateRuleFragment("Entry", position)
//            findNavController().navigate(action)
//            false
//        }
//
//        fastAdapter.onLongClickListener = { view, adapter, item, position ->
//            val card = view as MaterialCardView
//            when(actionMode) {
//                null -> {
//                    actionMode = activity?.startActionMode(actionModeCallback)
//                    card.isChecked = !card.isChecked
//                    true
//                }
//                else -> false
//            }
//        }

        createStrategyBinding.stratAppBar.title = resources.getText(R.string.text_view_entry_strat)
        createStrategyBinding.addRuleButton.setOnClickListener {
            val action = EntryFragmentDirections.actionEntryFragmentToCreateRuleFragment("Entry")
            findNavController().navigate(action)
        }
        createStrategyBinding.stratAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.action_confirm_strat -> {
                    val action = EntryFragmentDirections.actionEntryFragmentToExitFragment()
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
            val action = EntryFragmentDirections.actionEntryFragmentToCreateRuleFragment("Entry", position)
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
                viewModel.entryRulesInput,
                strategyAdapter
            )
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
            for (i in 0 until viewModel.entryRulesInput.size) {
                val card = createStrategyBinding.stratList.layoutManager!!.findViewByPosition(i) as MaterialCardView
                card.isChecked = false
            }
        }
    }
}