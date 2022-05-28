package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.alphabet.adapter.setUpSymbolSearch
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.database.PortfolioResultSchema
import com.example.alphabet.databinding.FragmentPortfolioInputBinding
import com.example.alphabet.databinding.PortfolioInputRowBinding
import com.example.alphabet.viewmodel.PortfolioViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.dataframe.api.*
import java.util.*

class PortfolioInputFragment: Fragment() {
    private val viewModel: StrategyViewModel by activityViewModels()
    private val portfolioViewModel: PortfolioViewModel by navGraphViewModels(R.id.nav_graph_port)
    private lateinit var databaseViewModel: DatabaseViewModel
    private var _binding: FragmentPortfolioInputBinding? = null
    private val binding get() = _binding!!
    private val args: PortfolioInputFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)
        _binding = FragmentPortfolioInputBinding.inflate(inflater, container, false)

        args.portResult?.apply {
            this.portfolioInputList.forEach {
                portfolioViewModel.symbolWeightingMap[it.stock.symbol] = it
            }
            viewModel.start.value = this.date.first().toCalendar()
            viewModel.end.value = this.date.last().toCalendar()
        }


        portfolioViewModel.symbolWeightingMap.forEach { (_, portfolioInput) ->
            addSymbol(portfolioInput)
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.confirm_button -> {
                    if(isInputValid()) {
                        this.hideKeyboard()
                        toPortfolioResult()
                    }
                    true
                }
                R.id.set_time_period -> {
                    val action = PortfolioInputFragmentDirections.actionGlobalTimePeriodBottomSheetFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        setUpSymbolSearch(binding.symbolText) { stock ->
            when (val symbol = stock.symbol) {
                in portfolioViewModel.symbolWeightingMap -> {
                    binding.symbolTextLayout.error = getString(R.string.duplicate_symbol_error)
                }
                else -> {
                    val portfolioInput = PortfolioInput(stock, "")
                    portfolioViewModel.symbolWeightingMap[symbol] = portfolioInput
                    binding.symbolText.setText("")
                    addSymbol(portfolioInput)
                    binding.symbolTextLayout.error = null
                }
            }

        }

        return binding.root
    }

    private fun addSymbol(portfolioInput: PortfolioInput) {
        val row = PortfolioInputRowBinding.inflate(LayoutInflater.from(requireContext()))
        val symbol = portfolioInput.stock.symbol
        val name = portfolioInput.stock.longname
        val weight = portfolioInput.weight

        row.symbolText.text = symbol
        row.stockNameText.text = name
        row.buttonDeletePortRow.setOnClickListener {
            portfolioViewModel.symbolWeightingMap.remove(symbol)
            binding.symbolWeightingList.removeView(row.root)
            if (portfolioViewModel.symbolWeightingMap.isEmpty())
                binding.viewEmptyHolding.root.visibility = View.VISIBLE
        }
        row.weightingText.id = View.generateViewId()  // ensure text save correctly
        row.weightingText.setText(weight)
        row.weightingText.doAfterTextChanged {
            portfolioViewModel.symbolWeightingMap[symbol]?.weight = it.toString()
        }
        binding.symbolWeightingList.addView(row.root)
        binding.viewEmptyHolding.root.visibility = View.GONE
    }

    private fun isInputValid(): Boolean {
        if (portfolioViewModel.symbolWeightingMap.isEmpty()) {
            binding.symbolTextLayout.error = "Please input at least one symbol"
            return false
        } else {
            portfolioViewModel.symbolWeightingMap.forEach { (_, port) ->
                if (port.weight.isEmpty()) {
                    return false
                }
            }
        }
        return true
    }

    private fun toPortfolioResult() {
        lifecycleScope.launch {
            binding.progressBarPort.isVisible = true
            binding.layoutPortInput.isVisible = false
            binding.topAppBar.isVisible = false

            val df = getClosePrice(
                portfolioViewModel.symbolWeightingMap.keys.toList(),
                viewModel.start.value!!,
                viewModel.end.value!!)
            if(df != null) {
                val dates = df["date"].toList().map { (it as Calendar).time }
                val symbolWeightingMapFloat = portfolioViewModel.symbolWeightingMap.mapValues { it.value.weight.toFloat() }
                val navList = df.update { dfsOf<Float>() }
                    .perRowCol { row, col ->
                        df[col][row.index()] / df[col][0] * symbolWeightingMapFloat[col.name()]!!
                    }
                    .remove("date")
                    .add("portRet") { rowSum() } ["portRet"]
                    .toList()
                    .map { it as Float }

                val portfolioInputList = portfolioViewModel.symbolWeightingMap.values.toList()
                val date = dates.map { MyApplication.sdfISO.format(it) }

                val portResult = args.portResult?.run {
                    val newPortResult = PortfolioResultSchema(
                        this.id, this.name, portfolioInputList, date, navList
                    )
                    databaseViewModel.updatePortfolioResult(newPortResult)
                    newPortResult
                } ?: PortfolioResultSchema(
                    id = 0,
                    name = "",
                    portfolioInputList = portfolioViewModel.symbolWeightingMap.values.toList(),
                    date = dates.map { MyApplication.sdfISO.format(it) },
                    nav = navList
                )

                val action = PortfolioInputFragmentDirections.actionGlobalPortfolioResultFragment(portResult)
                findNavController().navigate(action)

            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.failed_to_download_data_error)
                    .setPositiveButton("OK", null)
                    .show()
            }
            binding.progressBarPort.visibility = View.GONE
            binding.layoutPortInput.visibility = View.VISIBLE
            binding.topAppBar.visibility = View.VISIBLE
        }
    }
}