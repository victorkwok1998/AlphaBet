package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.alphabet.adapter.setUpSymbolSearch
import com.example.alphabet.databinding.FragmentHedgeInputBinding
import com.example.alphabet.viewmodel.HedgeViewModel
import kotlinx.coroutines.launch

class HedgeInputFragment: Fragment() {
    private var _binding: FragmentHedgeInputBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HedgeViewModel by navGraphViewModels(R.id.nav_graph_hedge)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHedgeInputBinding.inflate(inflater, container, false)

        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.confirm_button -> {
                    onConfirm()
                    true
                }
                else -> false
            }
        }
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        viewModel.hedgePort.observe(viewLifecycleOwner) { port ->
            port?.let { binding.etPortName.setText(port.name) }
        }
        binding.etPortName.setOnClickListener {
            val action = HedgeInputFragmentDirections.actionFragmentHedgeInputToPortfolioSelectFragment()
            findNavController().navigate(action)
        }
        setUpSymbolSearch(binding.etSymbol) {
            viewModel.stock.value = it
        }
        viewModel.stock.observe(viewLifecycleOwner) { stock ->
            stock?.apply {
                binding.layoutSelectedStock.root.isVisible = true
                with(binding.layoutSelectedStock) {
                    textSearchSymbol.text = stock.symbol
                    textSearchExchDisp.text = stock.exchDisp
                    textSearchTypeDisp.text = stock.typeDisp
                    textSearchName.text = stock.longname ?: stock.shortname
                }
            }
        }
        return binding.root
    }

    private fun onConfirm() {
        lifecycleScope.launch {
            var isValid = true
            if (viewModel.hedgePort.value == null){
                binding.textLayoutSelectPort.error = getString(R.string.required)
                isValid = false
            }
            val symbol = viewModel.stock.value?.symbol ?: ""
            if (symbol.isEmpty()) {
                binding.textLayoutHedgeSymbol.error = getString(R.string.required)
                isValid = false
            }
            if (isValid){
                val action = HedgeInputFragmentDirections.actionHedgeInputFragmentToHedgeResultFragment(symbol, viewModel.hedgePort.value!!)
                findNavController().navigate(action)
            }
        }
    }
}