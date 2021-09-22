//package com.example.alphabet
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.AdapterView
//import android.widget.ArrayAdapter
//import androidx.fragment.app.activityViewModels
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
//import androidx.navigation.navGraphViewModels
//import com.example.alphabet.databinding.FragmentCreateRuleBinding
//import com.example.alphabet.databinding.SelectIndicatorBinding
//
//class CreateRuleFragment : Fragment(R.layout.fragment_create_rule) {
//    private var _binding: FragmentCreateRuleBinding? = null
//    private val binding get() = _binding!!
//    private val safeArgs: CreateRuleFragmentArgs by navArgs()
//    private val viewModel: StrategyViewModel by navGraphViewModels(R.id.strategy_nav_graph)
//    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
//    private val indList: List<String> by lazy {
//        staticDataViewModel.indToParamList.value!!.keys.toList().sorted()
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentCreateRuleBinding.inflate(inflater, container, false)
//        val selectIndicatorBinding1 = SelectIndicatorBinding.bind(binding.indicator1Layout)
//        val selectIndicatorBinding2 = SelectIndicatorBinding.bind(binding.indicator2Layout)
//
//        selectIndicatorBinding1.specificValueButton.visibility = View.GONE
//        for (selectIndicatorBinding in listOf(selectIndicatorBinding1, selectIndicatorBinding2)) {
//            setSpinnerAdapter(selectIndicatorBinding, indList)
//            setOnIndSelected(selectIndicatorBinding)
//            setOnTypeClicked(selectIndicatorBinding)
//        }
//        // position = -1 -> add new rule
//        // position >= 0 -> edit rule
//        if (safeArgs.position >= 0) {
//            val ruleInput = when(safeArgs.from) {
//                "Entry" -> viewModel.entryRulesInput[safeArgs.position]
//                else -> viewModel.exitRulesInput[safeArgs.position]
//            }
//            when (ruleInput.condName) {
//                Cond.CROSS_UP -> binding.crossupButton.performClick()
//                Cond.CROSS_DOWN -> binding.crossdownButton.performClick()
//                Cond.OVER -> binding.overButton.performClick()
//                Cond.UNDER -> binding.underButton.performClick()
//            }
//            fillIndicator(ruleInput.indInput1, selectIndicatorBinding1)
//            fillIndicator(ruleInput.indInput2, selectIndicatorBinding2)
//        }
//
//        binding.ruleAppBar.setOnMenuItemClickListener {
//            when(it.itemId) {
//                R.id.action_confirm_rule -> {
//                    val ruleInput = createRuleInput(selectIndicatorBinding1, selectIndicatorBinding2, binding)
//                    var action = CreateRuleFragmentDirections.actionCreateRuleFragmentToEntryFragment()
//                    if (safeArgs.from == "Entry") {
//                        if (safeArgs.position == -1)
//                            viewModel.entryRulesInput.add(ruleInput)
//                        else
//                            viewModel.entryRulesInput[safeArgs.position] = ruleInput
//                    } else {
//                        if (safeArgs.position == -1)
//                            viewModel.exitRulesInput.add(ruleInput)
//                        else
//                            viewModel.exitRulesInput[safeArgs.position] = ruleInput
//                        action = CreateRuleFragmentDirections.actionCreateRuleFragmentToExitFragment()
//                    }
//                    findNavController().navigate(action)
//                    true
//                }
//                else -> false
//            }
//        }
//        return binding.root
//    }
//
//    private fun setOnTypeClicked(selectIndicatorBinding: SelectIndicatorBinding) {
//        with (selectIndicatorBinding) {
//            typeRadioGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
//                if (checkedId == R.id.specific_value_button) {
//                    indicatorLayout.visibility = View.GONE
//                    specificValueTextLayout.visibility = View.VISIBLE
//                }
//                if (checkedId == R.id.indicator_button) {
//                    specificValueTextLayout.visibility = View.GONE
//                    indicatorLayout.visibility = View.VISIBLE
//                }
//                if (checkedId == R.id.price_button) {
//                    specificValueTextLayout.visibility = View.GONE
//                    indicatorLayout.visibility = View.GONE
//                }
//            }
//        }
//    }
//
//    private fun setSpinnerAdapter(selectIndicatorBinding: SelectIndicatorBinding, stringList: List<String>) {
//        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, stringList)
//            .also { adapter ->
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                selectIndicatorBinding.indicatorSpinner.adapter = adapter
//            }
//    }
//
//    private fun setOnIndSelected(selectIndicatorBinding: SelectIndicatorBinding, paramList: List<Int>? = null) {
//        selectIndicatorBinding.indicatorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                val selected = parent?.getItemAtPosition(position).toString()
//                setParamInput(
//                    selectIndicatorBinding.indicatorParamListLayout,
//                    paramList,
//                    staticDataViewModel.indToParamList.value!![selected]!!,
//                    layoutInflater
//                )
//                // Replace all param input by new one
////                selectIndicatorBinding.indicatorParamListLayout.removeAllViews()
////                staticDataViewModel.indToParamList.value!![selected]?.forEachIndexed { i, v ->
////                    IndicatorParamBinding.inflate(layoutInflater, selectIndicatorBinding.indicatorParamListLayout, false).apply {
////                        this.paramValueText.hint = v
////                        // fill param list if not null
////                        paramList?.let { this.paramValueText.editText?.setText(it[i].toString()) }
////                        selectIndicatorBinding.indicatorParamListLayout.addView(this.root)
////                    }
////                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("Not yet implemented")
//            }
//        }
//    }
//
//    private fun fillIndicator(indicatorInput: IndicatorInput, selectIndicatorBinding: SelectIndicatorBinding) {
//        when (indicatorInput.indType) {
//            IndType.INDICATOR -> {
//                selectIndicatorBinding.indicatorButton.performClick()
//                selectIndicatorBinding.indicatorSpinner.setSelection(indList.indexOf(indicatorInput.indName))
//                setOnIndSelected(selectIndicatorBinding, indicatorInput.indParamList)
//            }
//            IndType.PRICE -> selectIndicatorBinding.priceButton.performClick()
//            IndType.VALUE -> {
//                selectIndicatorBinding.specificValueButton.performClick()
//                selectIndicatorBinding.specificValueText.setText(indicatorInput.indName)
//            }
//        }
//    }
//}