//package com.example.alphabet
//
//import android.app.DatePickerDialog
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import android.widget.LinearLayout
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.fragment.findNavbController
//import androidx.navigation.fragment.navArgs
//import androidx.navigation.navGraphViewModels
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import yahoofinance.Stock
//import yahoofinance.YahooFinance
//import java.io.FileNotFoundException
//import java.util.*
//import com.example.alphabet.MyApplication.Companion.sdf
//import com.example.alphabet.databinding.FragmentStrategyDescBinding
//
//class StrategyDescFragment: Fragment(R.layout.fragment_strategy_desc) {
//    private var _binding: FragmentStrategyDescBinding? = null
//    private val binding get() = _binding!!
//    private val viewModel: StrategyViewModel by navGraphViewModels(R.id.strategy_nav_graph)
//    private val safeArgs: StrategyDescFragmentArgs by navArgs()
//    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentStrategyDescBinding.inflate(inflater, container, false)
//        binding.stratAppBar.title = safeArgs.stratName.fullName + " Strategy"
////        binding.stratDes.text = staticDataViewModel.defaultStrategy.value!![safeArgs.stratName]!!.des
//
//        binding.advOpts.visibility = when (safeArgs.stratName) {
//            StrategyName.CUSTOM -> View.GONE
//            else -> View.VISIBLE
//        }
//        binding.advOptsParamLayout.visibility = View.GONE
//
//        binding.advOptsParamToggle.setOnClickListener {
//            when (binding.advOptsParamLayout.visibility) {
//                View.VISIBLE -> {
//                    binding.advOptsParamLayout.visibility = View.GONE
//                    binding.advOptsParamToggle.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
//                }
//                else -> {
//                    binding.advOptsParamLayout.visibility = View.VISIBLE
//                    binding.advOptsParamToggle.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
//                }
//            }
//        }
//
//        // Fill advanced options
//        if (safeArgs.stratName != StrategyName.CUSTOM) {
//            val strategyInput = staticDataViewModel.defaultStrategy.value!![safeArgs.stratName]
//            strategyInput?.apply {
//                if (entryRulesInput[0].indInput2.indType != IndType.VALUE) {
//                    binding.stratParamLayout.visibility = View.GONE
//                } else {
//                    binding.entryValueText.setText(entryRulesInput[0].indInput2.indName)
//                    binding.exitValueText.setText(exitRulesInput[0].indInput2.indName)
//                }
//                setParamInput(binding.indParamEntryLayout1, entryRulesInput[0].indInput1)
//                setParamInput(binding.indParamEntryLayout2, entryRulesInput[0].indInput2)
//                setParamInput(binding.indParamExitLayout1, exitRulesInput[0].indInput1)
//                setParamInput(binding.indParamExitLayout2, exitRulesInput[0].indInput2)
//
//                viewModel.entryRulesInput = entryRulesInput
//                viewModel.exitRulesInput = exitRulesInput
//                viewModel.stopGain.value = stopGain
//                viewModel.stopLoss.value = stopLoss
//            }
//
//
//        }
//
//        binding.stratAppBar.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.action_confirm_strat -> {
//                    when (safeArgs.stratName) {
//                        StrategyName.CUSTOM -> {
//                            val action = StrategyDescFragmentDirections.actionSelectSymbolFragmentToEntryFragment()
//                            findNavController().navigate(action)
//                        }
//                        else -> {
//                            viewModel.stopGain.value =
//                                binding.stopGainText.text.toString().toFloatOrNull()
//                            viewModel.stopLoss.value =
//                                binding.stopLossText.text.toString().toFloatOrNull()
//
//                            listOf(
//                                viewModel.entryRulesInput[0].indInput1,
//                                viewModel.entryRulesInput[0].indInput2,
//                                viewModel.exitRulesInput[0].indInput1,
//                                viewModel.exitRulesInput[0].indInput2
//                            ).zip(
//                                listOf(
//                                    binding.indParamEntryLayout1,
//                                    binding.indParamEntryLayout2,
//                                    binding.indParamExitLayout1,
//                                    binding.indParamExitLayout2
//                                )
//                            ).forEachIndexed { i, (indicatorInput, layout) ->
//                                when {
//                                    indicatorInput.indType == IndType.INDICATOR ->
//                                        indicatorInput.indParamList =
//                                            readParamListFromLayout(layout)
//                                    // VALUE and Entry
//                                    indicatorInput.indType == IndType.VALUE && i < 2 ->
//                                        indicatorInput.indName =
//                                            binding.entryValueText.text.toString()
//                                    // VALUE and Exit
//                                    indicatorInput.indType == IndType.VALUE && i >= 2 ->
//                                        indicatorInput.indName =
//                                            binding.exitValueText.text.toString()
//                                }
//                            }
//                            val action = StrategyDescFragmentDirections.actionStrategyDescFragmentToBacktestResultFragment()
//                            findNavController().navigate(action)
//                        }
//                    }
//                    true
//                }
//                else -> false
//            }
//
//        }
//        return binding.root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    private fun setParamInput(layout: LinearLayout, indicatorInput: IndicatorInput) {
//        if (indicatorInput.indParamList.isNotEmpty()) {
//            setParamInput(
//                layout,
//                indicatorInput.indParamList,
//                staticDataViewModel.indToParamList.value!!.getOrDefault(indicatorInput.indName, listOf()),
//                layoutInflater
//            )
//        }
//
//    }
//}