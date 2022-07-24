package com.example.alphabet

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.alphabet.MyApplication.Companion.sdfLong
import com.example.alphabet.adapter.setUpSymbolSearch
import com.example.alphabet.databinding.FragmentBacktestInputBinding
import com.example.alphabet.databinding.StockRowBinding
import com.example.alphabet.viewmodel.BacktestViewModel
import java.util.*

class BacktestInputFragment: Fragment() {
    private val viewModel: BacktestViewModel by navGraphViewModels(R.id.nav_graph_backtest)
    private var _binding:FragmentBacktestInputBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBacktestInputBinding.inflate(inflater, container, false)

        binding.topAppBar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.set_time_period -> {
                    val action = BacktestInputFragmentDirections.actionGlobalTimePeriodBottomSheetFragment()
                    findNavController().navigate(action)
                    true
                }
                R.id.confirm_button -> {
                    if (viewModel.stockList.isEmpty()) {
                        binding.symbolTextLayout.error = getString(R.string.required)
                    } else {
                        val action = BacktestInputFragmentDirections.actionBacktestInputFragmentToBacktestStrategyInputFragment()
                        findNavController().navigate(action)
                    }
                    true
                }
                else -> false
            }
        }

//        viewModel.strategyList.observe(viewLifecycleOwner) {
//
//        }
//        binding.strategyRv.removeAllViews()
//        viewModel.checkedStrategy.values.forEachIndexed { i, v ->
//            val row = BacktestStrategyInputRowBinding.inflate(inflater, binding.strategyRv, false)
//            row.textStrategy.setText(v.strategy.strategyName)
//            row.buttonDelete.setOnClickListener {
////                viewModel.strategyList.value = viewModel.strategyList.value?.filterIndexed { index, _ -> index != binding.strategyRv.indexOfChild(row.root) }
//                binding.strategyRv.removeView(row.root)
//            }
//            binding.strategyRv.addView(row.root)
//        }
//        binding.createStrategyButton.setOnClickListener {
//            val action =
//                BacktestInputFragmentDirections.actionBacktestInputFragmentToSelectStrategyFragment(-1)
//            findNavController().navigate(action)
//        }

        viewModel.stockList.forEach { addSymbolRow(it) }

        setUpSymbolSearch(binding.symbolText) { stock ->
            if (stock in viewModel.stockList) {
                binding.symbolTextLayout.error = getString(R.string.duplicate_symbol_error)
            } else {
                addSymbolRow(stock)
                viewModel.stockList.add(stock)
            }
        }
//        binding.symbolText.doAfterTextChanged { v ->
//            val text = v?.toString() ?: ""
//            lifecycleScope.launch {
//                if (text.isNotEmpty()) {
//                    val r = try {
//                        RetrofitInstance.api.searchSymbol(text)
//                    } catch (e: Exception) {
//                        Log.e("Yahoo Finance API", e.toString())
//                        return@launch
//                    }
//                    if (r.isSuccessful && r.body() != null) {
//                        val results = r.body()!!.quotes
//                            .filter { it.longname != null || it.shortname != null }
//                        val adapter = SymbolSearchAdapter(requireContext(), results)
//                        binding.symbolText.setAdapter(adapter)
//                        binding.symbolText.setOnItemClickListener { adapterView, view, i, l ->
//                            val stock = adapter.getItem(i)
//                            binding.symbolText.setText("")
//                            if (stock in viewModel.stockList) {
//                                binding.symbolTextLayout.error = "You've already added this symbol"
//                            } else {
//                                addSymbolRow(stock)
//                                viewModel.stockList.add(stock)
//                            }
//                        }
//                        binding.symbolText.showDropDown()
//                    }
//                }
//            }
//        }


        return binding.root
    }

    private fun addSymbolRow(stock: StockStatic) {
        binding.viewEmptySymbolList.root.isVisible = false
        val row = StockRowBinding.inflate(LayoutInflater.from(requireContext()))
        row.textSymbol.text = stock.symbol
        row.textStockName.text = stock.longname ?: stock.shortname
        row.textTypeDisp.text = stock.typeDisp
        row.buttonDelete.setOnClickListener {
            binding.layoutStockList.removeView(row.root)
            viewModel.stockList.remove(stock)
            if (viewModel.stockList.isEmpty()) {
                binding.viewEmptySymbolList.root.isVisible = true
            }
        }
        binding.layoutStockList.addView(row.root)
    }

//    private fun addSymbol(textView: TextView) {
//        lifecycleScope.launch {
//            val symbol = textView.text.toString().uppercase()
//            val stock = getStock(symbol)
//
//            if (stock != null) {
//                if (stock in viewModel.stockList) {
//                    binding.symbolTextLayout.error = "You've already input this symbol"
//                } else {
//                    addSymbolRow(stock)
//                    viewModel.stockList.add(stock)
//                    textView.text = ""
//                    binding.symbolTextLayout.error = null
//                }
//            } else {
//                binding.symbolTextLayout.error = "Invalid symbol"
//            }
//        }
//    }

    private fun setDatePicker(editText: EditText, c: Calendar) {
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        if (editText.text.toString().isEmpty()) {
            editText.setText(sdfLong.format(c.time))
        }

        editText.setOnClickListener {
            val dpd = DatePickerDialog(requireContext(), R.style.MySpinnerDatePickerStyle, { view, mYear, mMonth, mDay ->
                val cal = createCalendar(mYear, mMonth, mDay)
                editText.setText(sdfLong.format(cal.time))
            }, year, month, day)
            dpd.show()
        }
    }
}