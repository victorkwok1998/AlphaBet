package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alphabet.databinding.FragmentCreateRuleBinding
import com.example.alphabet.databinding.ParamDisplayRowBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CreateRuleFragment: Fragment() {
    private val viewModel: StrategyViewModel by activityViewModels()
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private var _binding: FragmentCreateRuleBinding? = null
    private val binding get() = _binding!!
    private val args: CreateRuleFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateRuleBinding.inflate(inflater, container, false)

//        return ComposeView(requireContext()).apply {
//            setContent {
//                CreateRuleScreen(selectedRule, primaryParamNames, secondaryParamNames)
//            }
//        }
        val discardAlert = {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Discard changes?")
                .setNegativeButton("Cancel") { _, _ -> }
                .setPositiveButton("Discard") { dialog, which ->
                    findNavController().popBackStack()
                }
                .show()
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    discardAlert()
                }
            })
        binding.createRuleAppBar.setNavigationOnClickListener {
//            discardAlert()
            findNavController().popBackStack()
        }
//        binding.ruleTypeTabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                when (tab?.position) {
//                    0 -> {
//                        binding.nonIndRuleLayout.nonIndRuleLayout.visibility = View.GONE
//                        binding.createRuleLayout.visibility = View.VISIBLE
//                    }
//                    1 -> {
//                        binding.nonIndRuleLayout.nonIndRuleLayout.visibility = View.VISIBLE
//                        binding.createRuleLayout.visibility = View.GONE
//                    }
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//            }
//        })

        binding.conditionChipGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.cross_up_chip -> {
                    viewModel.cond.value = Cond.CROSS_UP
                }
                R.id.cross_down_chip -> {
                    viewModel.cond.value = Cond.CROSS_DOWN
                }
                R.id.over_chip -> {
                    viewModel.cond.value = Cond.OVER
                }
                else -> {
                    viewModel.cond.value = Cond.UNDER
                }
            }
        }

        binding.createRuleAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.confirm_button -> {
                    val cond = viewModel.cond.value!!
//                    val cond = when (binding.conditionChipGroup.checkedChipId) {
//                        R.id.cross_up_chip -> Cond.CROSS_UP
//                        R.id.cross_down_chip -> Cond.CROSS_DOWN
//                        R.id.over_chip -> Cond.OVER
//                        else -> Cond.UNDER
//                    }

                    val ind2Type = when(binding.indicatorChipGroup.checkedChipId) {
                        R.id.indicator_chip -> IndType.INDICATOR
                        R.id.constant_chip -> IndType.VALUE
                        else -> IndType.OTHER
                    }

                    if (isRuleInputValid(ind2Type)){
                        val primaryInd = viewModel.primaryInd.value!!
                        val secondaryInd = when(ind2Type) {
                            IndType.INDICATOR -> viewModel.secondaryInd.value!!
                            else -> IndicatorInput(ind2Type, "", mutableListOf(binding.constantIndicatorText.text.toString()))
                        }
                        val ruleInput = RuleInput(primaryInd, secondaryInd, cond)

                        if (args.position == -1) {
                            if (args.entryExit == EntryExit.ENTRY)
                                viewModel.customStrategy.value.entryRulesInput.add(ruleInput)
                            else
                                viewModel.customStrategy.value.exitRulesInput.add(ruleInput)
                        } else {
                            if (args.entryExit == EntryExit.ENTRY)
                                viewModel.customStrategy.value.entryRulesInput[args.position] = ruleInput
                            else
                                viewModel.customStrategy.value.exitRulesInput[args.position] = ruleInput
                        }
                        findNavController().popBackStack()
                    }

                    true
                }
                else -> false
            }
        }

        binding.indicatorChipGroup.setOnCheckedChangeListener { group, checkedId ->
            if (viewModel.primaryInd.value?.indType != IndType.OTHER) {
                setIndicatorChipVisibility()
//                when(checkedId) {
//                    R.id.indicator_chip -> {
//                        binding.secondaryIndicatorLayout.visibility = View.VISIBLE
//                        // trigger onIndNameChange if indicator chip is checked
//                        viewModel.secondaryInd.value?.apply {
//                            onIndNameChange(this, PrimSec.SECONDARY, layoutInflater)
//                        }
//                        binding.constantIndicatorLayout.visibility = View.GONE
//                    }
//                    R.id.constant_chip -> {
//                        binding.secondaryIndicatorLayout.visibility = View.GONE
//                        binding.secondaryParamDisplay.paramDisplayMainLayout.visibility = View.GONE
//                        binding.constantIndicatorLayout.visibility = View.VISIBLE
//                    }
//                }
            }
        }

        binding.primaryIndicatorText.setOnClickListener {
            val action = CreateRuleFragmentDirections.actionCreateRuleFragmentToIndicatorListFragment(PrimSec.PRIMARY)
            findNavController().navigate(action)
        }
        binding.secondaryIndicatorText.setOnClickListener {
            val action = CreateRuleFragmentDirections.actionCreateRuleFragmentToIndicatorListFragment(PrimSec.SECONDARY)
            findNavController().navigate(action)
        }
        viewModel.primaryInd.observe(viewLifecycleOwner) {
//            val paramValues = viewModel.primaryIndParams.value
            displayParamLayout(it, PrimSec.PRIMARY, layoutInflater)
            if (it.indType == IndType.OTHER) {
                binding.conditionGroup.visibility = View.GONE
                binding.secondaryIndicatorGroup.visibility = View.GONE
                binding.secondaryIndicatorLayout.visibility = View.GONE
                binding.secondaryParamDisplay.paramDisplayMainLayout.visibility = View.GONE
                binding.constantIndicatorLayout.visibility = View.GONE
            }

            binding.primaryIndicatorText.setText(it.indName)
        }
        viewModel.secondaryInd.observe(viewLifecycleOwner) {
//            val paramValues = viewModel.secondaryIndParams.value
            displayParamLayout(it, PrimSec.SECONDARY, layoutInflater)

            if(it.indType == IndType.VALUE) {
                binding.constantChip.isChecked = true
                binding.constantIndicatorText.setText(it.indParamList[0])
            } else {
                binding.secondaryIndicatorText.setText(it.indName)
            }
        }
        viewModel.cond.observe(viewLifecycleOwner) {
            when(it) {
                Cond.CROSS_UP -> {binding.crossUpChip.isChecked = true}
                Cond.CROSS_DOWN -> {binding.crossDownChip.isChecked = true}
                Cond.OVER -> {binding.overChip.isChecked = true}
                Cond.UNDER -> {binding.underChip.isChecked = true}
                else -> {}
            }
        }
        binding.constantIndicatorLayout.setEndIconOnClickListener {
            binding.constantIndicatorText.setText("")
        }

        return binding.root
    }

    private fun isRuleInputValid(
        secondaryIndType: IndType
    ): Boolean {
        var res = true

        if (binding.primaryIndicatorText.text.toString().isEmpty()){
            binding.primaryIndicatorTextLayout.error = "Please select an indicator"
            res = false
        }
        else if (viewModel.primaryInd.value?.indType == IndType.OTHER)
            return true
        else
            binding.primaryIndicatorTextLayout.error = null

        when {
            secondaryIndType == IndType.INDICATOR && binding.secondaryIndicatorText.text.toString().isEmpty() -> {
                binding.secondaryIndicatorLayout.error = "Please select an indicator"
                res = false
            }
            secondaryIndType == IndType.VALUE && binding.constantIndicatorText.text.toString().isEmpty() -> {
                binding.constantIndicatorLayout.error = "Please input a value"
                res = false
            }
        }
        return res
    }

//    private fun isOtherRuleValid(): Boolean {
//        var res = true
//        if (binding.nonIndRuleLayout.nonIndRuleNameText.toString().isEmpty()) {
//            binding.nonIndRuleLayout.nonIndRuleNameText.error = "Please select a rule"
//            res = false
//        } else {
//            binding.nonIndRuleLayout.nonIndRuleNameTextLayout.error = null
//        }
//        val paramLayout = binding.nonIndRuleLayout.nonIndRuleParamLayout
//        getIndicatorParam(paramLayout).forEachIndexed { i, text ->
//            if (text.isEmpty()) {
//                (paramLayout.getChildAt(i) as TextInputLayout).error = "Please input a value "
//                res = false
//            }
//        }
//        return res
//    }

//    private fun getIndicatorParam(layout: LinearLayout): MutableList<String> {
//        return MutableList(layout.childCount) { i ->
//            val textInputLayout = layout.getChildAt(i) as TextInputLayout
//            textInputLayout.editText?.text.toString()
//        }
//    }

    private fun displayParamLayout(
        indicatorInput: IndicatorInput,
//        paramValues: List<String>?,
        primSec: PrimSec,
        inflater: LayoutInflater,
    ) {
        val paramValues = indicatorInput.indParamList

        val paramLayoutBinding = when(primSec) {
            PrimSec.PRIMARY -> binding.primaryParamDisplay
            PrimSec.SECONDARY -> binding.secondaryParamDisplay
        }
        val paramNames = staticDataViewModel.indToParamList.value[indicatorInput.indName]
//        val paramNames = indicatorInput.paramName
        val mainLayout = paramLayoutBinding.paramDisplayMainLayout
        val paramLayout = paramLayoutBinding.paramDisplayLayout

        if (paramNames != null && paramValues.isNotEmpty() && (primSec == PrimSec.PRIMARY || (primSec == PrimSec.SECONDARY && binding.indicatorChip.isChecked))) {
            paramLayout.removeAllViews()
            paramNames
                .zip(paramValues)
                .forEach { (paramName, paramValue) ->
                    val paramDisplayRow = ParamDisplayRowBinding.inflate(inflater, paramLayout, false)
                    paramDisplayRow.paramNameLabel.text = paramName
                    paramDisplayRow.paramValueText.text = paramValue
                    paramLayout.addView(paramDisplayRow.root)
                }

            paramLayoutBinding.paramEditButton.setOnClickListener {
                parameterDialog(indicatorInput, primSec, childFragmentManager, false)
            }
            mainLayout.visibility = View.VISIBLE
        } else {
            mainLayout.visibility = View.GONE
        }
    }

    private fun setIndicatorChipVisibility() {
        if (binding.indicatorChip.isChecked) {
            binding.secondaryIndicatorLayout.visibility = View.VISIBLE
            viewModel.secondaryInd.value?.apply {
                displayParamLayout(this, PrimSec.SECONDARY, layoutInflater)
            }
            binding.constantIndicatorLayout.visibility = View.GONE
        } else if (binding.constantChip.isChecked) {
            binding.secondaryIndicatorLayout.visibility = View.GONE
            binding.secondaryParamDisplay.paramDisplayMainLayout.visibility = View.GONE
            binding.constantIndicatorLayout.visibility = View.VISIBLE
        }
    }
    
//    @ExperimentalComposeUiApi
//    @Composable
//    fun CreateRuleScreen(
//        rule: IndicatorRuleInput,
//        primaryParamNames: List<String>,
//        secondParamNames: List<String>
//    ) {
//        Scaffold(
//            topBar = {
//                MyTopAppBar(
//                    title = { Text("Create Rule") },
//                    navigationIcon = {
//                        IconButton(onClick = { findNavController().popBackStack() }) {
//                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
//                        }
//                    }
//                )
//            }
//        ) {
//            Column(
//                Modifier.background(grayBackground)
//            ) {
//                CreateRule(
//                    primaryIndicator = rule.indInput1.indName,
//                    primaryParamNames = primaryParamNames,
//                    primaryParamValues = rule.indInput1.indParamList,
//                    primaryOnClick = {
////                        viewModel.selectedIndicator.value = viewModel.selectedRule.value.indInput1
//                        val action = CreateRuleFragmentDirections.actionCreateRuleFragmentToIndicatorListFragment()
//                        findNavController().navigate(action)
//                    },
//                    primaryOnValueChange = { index, value ->
//                        rule.indInput1.indParamList[index] = value
//                    },
//                    secondIndicator = rule.indInput2.indName,
//                    secondParamNames = secondParamNames,
//                    secondParamValues = rule.indInput2.indParamList,
//                    secondOnClick = {
////                        viewModel.selectedIndicator.value = viewModel.selectedRule.value.indInput2
//                        val action = CreateRuleFragmentDirections.actionCreateRuleFragmentToIndicatorListFragment()
//                        findNavController().navigate(action)
//                    },
//                    secondOnValueChange = { index, value ->
//                        rule.indInput2.indParamList[index] = value
//                    },
//                    condOnValueChange = {
//                        rule.condName = Cond.fromValue(it)
//                    }
//                )
//            }
//        }
//    }
}