package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.alphabet.databinding.FragmentPortfolioBinding
import com.example.alphabet.databinding.PortfolioRowBinding
import kotlinx.coroutines.launch

class PortfolioFragment: Fragment() {
    private val viewModel: StrategyViewModel by activityViewModels()
    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)

        binding.addSymbolWeightButton.setOnClickListener {
            val symbol = binding.symbolText.text.toString().uppercase()
            lifecycleScope.launch {
                when {
                    symbol in viewModel.symbolWeightingMap -> {
                        binding.symbolTextLayout.error = "You've already input this symbol"
                    }
                    symbol.isEmpty() -> {
                        binding.symbolTextLayout.error = "Please input a value"
                    }
                    else -> {
                        val stock = getStock(symbol)
                        if (stock == null) {
                            binding.symbolTextLayout.error = "Invalid symbol"
                        } else {
                            val portfolioInput = PortfolioInput(stock, "")
                            viewModel.symbolWeightingMap[symbol] = portfolioInput
                            binding.symbolText.setText("")
                            addSymbol(portfolioInput)
                            binding.symbolTextLayout.error = null
                        }
                    }
                }
            }
        }
        viewModel.symbolWeightingMap.forEach { (_, portfolioInput) ->
            addSymbol(portfolioInput)
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.confirm_button -> {
                    if(isInputValid()) {
                        val action = PortfolioFragmentDirections.actionPortfolioFragmentToPortfolioResultFragment()
                        findNavController().navigate(action)
                    }
                    true
                }
                R.id.set_time_period -> {
                    val action = PortfolioFragmentDirections.actionPortfolioFragmentToTimePeriodBottomSheetFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun addSymbol(portfolioInput: PortfolioInput) {
        val row = PortfolioRowBinding.inflate(LayoutInflater.from(requireContext()))
        val symbol = portfolioInput.stock.symbol
        val name = portfolioInput.stock.name
        val weight = portfolioInput.weight

        row.symbolText.text = symbol
        row.stockNameText.text = name
        row.deleteButton.setOnClickListener {
            viewModel.symbolWeightingMap.remove(symbol)
            binding.symbolWeightingList.removeView(row.root)
        }
        row.weightingText.id = View.generateViewId()  // ensure text save correctly
        row.weightingText.setText(weight)
        row.weightingText.doAfterTextChanged {
            viewModel.symbolWeightingMap[symbol]?.weight = it.toString()
        }
        binding.symbolWeightingList.addView(row.root)
    }

    private fun isInputValid(): Boolean {
        if (viewModel.symbolWeightingMap.isEmpty()) {
            binding.symbolTextLayout.error = "Please input at least one symbol"
            return false
        } else {
            viewModel.symbolWeightingMap.forEach { (_, port) ->
                if (port.weight.isEmpty()) {
                    return false
                }
            }
        }
        return true
    }
}