package com.example.alphabet

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.alphabet.databinding.FragmentBacktestInputBinding
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yahoofinance.Stock
import yahoofinance.YahooFinance
import java.io.FileNotFoundException
import java.util.*
import androidx.core.util.Pair
import androidx.fragment.app.FragmentActivity

//TODO: Recent Backtests
class BacktestInputFragment: Fragment(R.layout.fragment_backtest_input) {
    private var _binding: FragmentBacktestInputBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StrategyViewModel by navGraphViewModels(R.id.strategy_nav_graph)
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private lateinit var selectedStrategyName: StrategyName

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBacktestInputBinding.inflate(inflater, container, false)

        val defaultEnd = Calendar.getInstance().apply {add(Calendar.DATE, -1)}  // yesterday
        setDatePicker(binding.endDate, defaultEnd)

        val defaultStart = defaultEnd.apply { add(Calendar.YEAR, -1) }  // one year before
        setDatePicker(binding.startDate, defaultStart)

        val strategyNameList = StrategyName.values().map { requireContext().getString(R.string.strategy_name, it.fullName) }

        val adapter = ArrayAdapter(requireContext(), R.layout.strategy_list_item, strategyNameList)
        val actv = binding.strategyListDropdown.editText as AutoCompleteTextView

        actv.setAdapter(adapter)
        selectedStrategyName = StrategyName.values().first()
        actv.setText(strategyNameList.first(), false)
        setStrategyDes(selectedStrategyName)
        actv.setOnItemClickListener { parent, view, position, id ->
            selectedStrategyName = StrategyName.values()[position]
            setStrategyDes(selectedStrategyName)
            binding.backtestButton.visibility = if (selectedStrategyName == StrategyName.CUSTOM) View.GONE else View.VISIBLE
        }

        binding.backtestButton.setOnClickListener {
            processUserInput()
        }
        binding.advOptsButton.setOnClickListener {
            processUserInput(true)
        }

        return binding.root
    }

    private fun setDatePicker(editText: EditText, c: Calendar) {
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        if (editText.text.toString().isEmpty()) {
            editText.setText(MyApplication.sdfLong.format(c.time))
        }

        editText.setOnClickListener {
            val dpd = DatePickerDialog(requireContext(), R.style.MySpinnerDatePickerStyle, { view, mYear, mMonth, mDay ->
                val cal = createCalandar(mYear, mMonth, mDay)
                editText.setText(MyApplication.sdfLong.format(cal.time))
            }, year, month, day)
            dpd.show()
        }
    }

    private fun processUserInput(isAdvOpt: Boolean = false) {
        val symbol = binding.symbol.text.toString().uppercase()
        var stock: Stock? = null
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    stock = YahooFinance.get(symbol)
                } catch (e: FileNotFoundException) {
                    Log.e("YahooFinance", "Failed to get stock $symbol")
                }
            }
            when {
                stock == null -> Toast.makeText(context, "Invalid Symbol", Toast.LENGTH_LONG).show()
//                selectedStrategyName == null -> Toast.makeText(context, "Please select a strategy", Toast.LENGTH_LONG).show()
                else -> {
                    with(viewModel) {
                        this.symbol.value = symbol
                        this.start.value = Calendar.getInstance().apply {
                            time = MyApplication.sdfLong.parse(binding.startDate.text.toString())!!
                        }
                        this.end.value = Calendar.getInstance().apply {
                            time = MyApplication.sdfLong.parse(binding.endDate.text.toString())!!
                        }
                        val selectedStrategy = staticDataViewModel.defaultStrategy.value!![selectedStrategyName]!!
                        this.stratName.value = selectedStrategyName.fullName
                        this.entryRulesInput = selectedStrategy.entryRulesInput
                        this.exitRulesInput = selectedStrategy.exitRulesInput
                    }
                    when {
                        isAdvOpt -> {
                            val action = BacktestInputFragmentDirections.actionBacktestInputFragmentToEntryFragment()
                            findNavController().navigate(action)
                        }
                        else -> {
                            val action = BacktestInputFragmentDirections.actionBacktestInputFragmentToBacktestResultFragment()
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }
    private fun setStrategyDes(selectedStrategyName: StrategyName) {
        if (selectedStrategyName != StrategyName.CUSTOM) {
            viewModel.entryRulesInput =
                staticDataViewModel.defaultStrategy.value!![selectedStrategyName]!!.entryRulesInput
            viewModel.exitRulesInput =
                staticDataViewModel.defaultStrategy.value!![selectedStrategyName]!!.exitRulesInput
            val entryDes = viewModel.entryRulesDes()
            val exitDes = viewModel.exitRulesDes()
            binding.stratDes.text =
                resources.getString(R.string.strategy_des, entryDes, exitDes)
        } else {
            binding.stratDes.text = "Create your own strategy"
        }
    }
}