package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.alphabet.databinding.CardOptBinding
import com.example.alphabet.databinding.FragmentOptParamInputBinding
import com.example.alphabet.databinding.LayoutOptParamRowBinding
import com.example.alphabet.databinding.TvOptIndicatorBinding
import com.example.alphabet.viewmodel.OptViewModel

class OptParamInputFragment: Fragment() {
    private var _binding: FragmentOptParamInputBinding? = null
    private val binding get() = _binding!!
    private val args: OptParamInputFragmentArgs by navArgs()
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private val optViewModel: OptViewModel by navGraphViewModels(R.id.nav_graph_opt)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOptParamInputBinding.inflate(inflater, container, false)
        binding.topAppBar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.confirm_button -> {
                    onConfirm()
                    true
                }
                else -> false
            }
        }

        fun buildRuleLayout(
            linearLayout: LinearLayout,
            indicator: IndicatorInput
        ) {
            val paramNameList = staticDataViewModel.indToParamList[indicator.indName] ?: return
            val tv = TvOptIndicatorBinding.inflate(inflater).tvOptIndicator
            if (paramNameList.isEmpty()){
//                textView.text = getString(R.string.opt_no_param, indicator.indName)
                linearLayout.addView(
                    tv.apply { text = getString(R.string.opt_no_param, indicator.indName) }
                )
            } else {
//                textView.text = indicator.indName
                linearLayout.addView(
                    tv.apply { text = indicator.indName }
                )
                paramNameList.forEach { paramName ->
                    val paramRowBinding =
                        LayoutOptParamRowBinding.inflate(inflater, linearLayout, false)
                    paramRowBinding.switchOptParam.text = paramName
                    paramRowBinding.switchOptParam.setOnCheckedChangeListener { buttonView, isChecked ->
                        paramRowBinding.groupOptTextInput.isVisible = isChecked
                    }
                    paramRowBinding.etOptStep.doAfterTextChanged {
                        val step = it.toString().toFloatOrNull()
                        when {
                           step == null -> {
                               paramRowBinding.textInputLayoutStep.error = getString(R.string.invalid_number)
                           }
                           step <= 0 -> {
                               paramRowBinding.textInputLayoutStep.error = getString(R.string.not_positive_number)
                           }
                           else -> {paramRowBinding.textInputLayoutStep.error = null}
                        }
                    }
                    linearLayout.addView(paramRowBinding.root)
                    optViewModel.paramRowList.add(paramRowBinding)
                }
            }
        }
        fun buildOptCardLayout(
            cardOptBinding: CardOptBinding,
            rules: List<RuleInput>,
            title: String
        ) {
            cardOptBinding.tvOptTitle.text = title
            rules.forEachIndexed { i, rule ->
//                val ruleBinding = LayoutOptRuleRowBinding.inflate(inflater, cardOptBinding.layoutOptRuleList, false)
                buildRuleLayout(
                    cardOptBinding.layoutOptRuleList,
                    rule.indInput1
                )
                buildRuleLayout(
                    cardOptBinding.layoutOptRuleList,
                    rule.indInput2
                )
//                cardOptBinding.layoutOptRuleList.addView(ruleBinding.root)
            }
        }
        val strategy = args.strategy
        buildOptCardLayout(binding.cardOptEntry, strategy.entryRulesInput, getString(R.string.entry))
        buildOptCardLayout(binding.cardOptExit, strategy.exitRulesInput, getString(R.string.exit))
        return binding.root
    }

    private fun onConfirm() {
        val oriParamList = args.strategy.getParamList()
        val paramList = List(optViewModel.paramRowList.size) { i ->
            val v = optViewModel.paramRowList[i]
            val start = v.etOptStart.text.toString().toIntOrNull()
            val stop = v.etOptStop.text.toString().toIntOrNull()
            val step = v.etOptStep.text.toString().toIntOrNull()
            if (start != null && stop != null && step != null && v.groupOptTextInput.isVisible) {
                range(start, stop, step).map { it.toString() }
            } else {
                listOf(oriParamList[i])
            }
        }
        val strategyList = paramList.getCartesianProduct().map {
            args.strategy
                .copy()
                .apply {
                    setStrategyParamList(
                        paramList = it,
                        indToParamList = staticDataViewModel.indToParamList
                    )
                }
        }
        val action = OptParamInputFragmentDirections.actionOptParamInputFragmentToRunStrategyDialog(
            start = args.start,
            end = args.end,
            strategyList = strategyList.toTypedArray(),
            stockList = arrayOf(args.stock)
        )
        findNavController().navigate(action)
    }

    private fun range(start: Int, stop: Int, step: Int): List<Int> {
        val res = mutableListOf<Int>()
        var tmp = start
        while (stop >= tmp) {
            res.add(tmp)
            tmp += step
        }
        return res
    }
}