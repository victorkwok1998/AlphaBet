package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.database.StrategySchema
import com.example.alphabet.databinding.DialogRenameStrategyBinding
import com.example.alphabet.databinding.FieldDetailRowBinding
import com.example.alphabet.databinding.FragmentEditStrategyBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditStrategyFragment : Fragment() {
    private lateinit var databaseViewModel: DatabaseViewModel
    private var _binding: FragmentEditStrategyBinding? = null
    private val binding get() = _binding!!
    private val args: EditStrategyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val strategy = args.strategySchema.strategy
        _binding = FragmentEditStrategyBinding.inflate(inflater, container, false)
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)

        binding.topAppBar.apply {
            title = strategy.strategyName
            setNavigationOnClickListener {
                saveStrategy(args.strategySchema)
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.delete -> {
                        databaseViewModel.deleteStrategy(args.strategySchema)
                        Toast.makeText(requireContext(), "Strategy deleted", Toast.LENGTH_LONG)
                        findNavController().popBackStack()
                        true
                    }
                    R.id.rename -> {
                        val renameBinding = DialogRenameStrategyBinding.inflate(inflater)
                        renameBinding.layoutRenameStrategy.editText?.setText(strategy.strategyName)

                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Rename Strategy")
                            .setView(renameBinding.root)
                            .setPositiveButton("OK") {_, _ ->
                                renameBinding.layoutRenameStrategy.editText?.apply {
                                    val newName = this.text.toString()
                                    strategy.strategyName = newName
                                    databaseViewModel.updateStrategy(args.strategySchema)
                                    binding.topAppBar.title = newName
                                }
                            }
                            .setNegativeButton("Cancel") {_,_->}
                            .show()
                        true
                    }
                    else -> false
                }
            }
        }
        binding.textStrategyDes.text = strategy.des
        setRuleCard(EntryExit.ENTRY, args.strategySchema)
        setRuleCard(EntryExit.EXIT, args.strategySchema)
        return binding.root
    }

    private fun setRuleCard(entryExit: EntryExit, strategySchema: StrategySchema) {
        val strategy = strategySchema.strategy
        val cardBinding = if (entryExit == EntryExit.ENTRY) binding.cardEntry else binding.cardExit
        val ruleList = (if (entryExit == EntryExit.ENTRY) strategy.entryRulesInput else strategy.exitRulesInput)
            .filter { it.isValid() }
        cardBinding.buttonEditRule.setOnClickListener {
            val action = EditStrategyFragmentDirections.actionEditStrategyFragmentToEditRuleFragment(strategySchema, entryExit)
            findNavController().navigate(action)
        }
        if (ruleList.isNotEmpty()) {
            cardBinding.layoutRules.removeAllViews()
//            val drawable = when(entryExit) {
//                EntryExit.ENTRY -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_trending_up_24)
//                EntryExit.EXIT -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_trending_down_24)
//            }
            ruleList.forEachIndexed { i, rule ->
                val rowBinding = FieldDetailRowBinding.inflate(layoutInflater, cardBinding.layoutRules, false)
                rowBinding.textRow.text = rule.toString()
                rowBinding.textFieldName.text = getString(R.string.rule_title, i + 1)
                cardBinding.layoutRules.addView(rowBinding.root)
            }
        }
        cardBinding.textRuleCount.text = getString(R.string.rule_count, ruleList.size)
    }

    private fun saveStrategy(strategySchema: StrategySchema) {
        val newStrategy = strategySchema.apply {
            this.strategy.entryRulesInput.retainAll { it.isValid() }
            this.strategy.exitRulesInput.retainAll { it.isValid() }
        }
        if (newStrategy.strategy.entryRulesInput.isNotEmpty() && newStrategy.strategy.exitRulesInput.isNotEmpty())
            databaseViewModel.addStrategy(newStrategy)
    }
}
