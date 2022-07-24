package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.adapter.RuleCardAdapter
import com.example.alphabet.databinding.FragmentEditRuleBinding

class EditRuleFragment: Fragment() {
    private val args: EditRuleFragmentArgs by navArgs()
    private var _binding: FragmentEditRuleBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditRuleBinding.inflate(inflater, container, false)
        binding.topAppBar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.topAppBar.title = when(args.entryExit) {
            EntryExit.ENTRY -> "Entry Rules"
            EntryExit.EXIT -> "Exit Rules"
        }

        val adapter = RuleCardAdapter(
            onIndicatorClicked = { ind ->
                val action = EditRuleFragmentDirections.actionEditRuleFragmentToNavGraphIndicator(ind, args.entryExit)
                findNavController().navigate(action)
            },
            onEditParamClicked = { ind ->
                val action = EditRuleFragmentDirections.actionEditRuleFragmentToParameterDialogFragment(ind, ind)
                findNavController().navigate(action)
            },
            onDeleteClicked = { updateRules(it) }
        )
        binding.rvRules.adapter = adapter
        binding.rvRules.layoutManager = LinearLayoutManager(requireContext())
        when (args.entryExit) {
            EntryExit.ENTRY -> adapter.submitListCustom(args.strategySchema.strategy.entryRulesInput)
            EntryExit.EXIT -> adapter.submitListCustom(args.strategySchema.strategy.exitRulesInput)
        }

        binding.buttonAddRule.setOnClickListener {
            if (adapter.currentList.last().isValid()) {
                addRule(adapter)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please complete the previous rule",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.buttonDone.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun updateRules(newList: MutableList<RuleInput>) {
        when (args.entryExit) {
            EntryExit.ENTRY -> args.strategySchema.strategy.entryRulesInput = newList
            EntryExit.EXIT -> args.strategySchema.strategy.exitRulesInput = newList
        }
    }

//    private fun submitList(adapter: RuleCardAdapter, list: MutableList<RuleInput>) {
//        if (list.isEmpty()) {
//            list.add(RuleInput.getEmptyRule())
//        }
//        adapter.submitList(list)
//    }

    private fun addRule(adapter: RuleCardAdapter) {
        val newList = adapter.currentList.toMutableList()
        newList.add(
            RuleInput(
                IndicatorInput.getEmptyIndicator(),
                IndicatorInput.getEmptyIndicator(),
                Cond.CROSS_UP
            )
        )
        adapter.submitList(newList)
        updateRules(newList)
        binding.rvRules.smoothScrollToPosition(newList.lastIndex)
    }
}