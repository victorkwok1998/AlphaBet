package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.navGraphViewModels
import com.example.alphabet.databinding.FragmentBacktestAdvancedOptionsBinding
import com.example.alphabet.ui.setTimePeriod
import com.example.alphabet.viewmodel.BacktestViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BacktestAdvancedOptionsBottomSheet: BottomSheetDialogFragment() {
    private val viewModel: BacktestViewModel by navGraphViewModels(R.id.nav_graph_backtest)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBacktestAdvancedOptionsBinding.inflate(inflater, container, false)
        binding.layoutStrategyBacktestPeriod.setTimePeriod(
            requireContext(),
            viewModel.start,
            viewModel.end,
            viewLifecycleOwner
        )

        val textTc = binding.layoutTransactionCost.textInputTransactionCost.editText!!
        textTc.setText(viewModel.transactionCost.value)
        textTc.doAfterTextChanged {
            viewModel.transactionCost.value = it.toString()
        }
        binding.layoutTransactionCost.buttonGroupCostType.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if(isChecked) {
                if (checkedId == R.id.button_bps)
                    viewModel.transactionCostType.value = CostType.BPS
                if (checkedId == R.id.button_pct)
                    viewModel.transactionCostType.value = CostType.PCT
                if (checkedId == R.id.button_fixed)
                    viewModel.transactionCostType.value = CostType.FIXED
            }
        }
        binding.layoutTransactionCost.buttonGroupCostType.check(R.id.button_bps)  // default
        return binding.root
    }
}