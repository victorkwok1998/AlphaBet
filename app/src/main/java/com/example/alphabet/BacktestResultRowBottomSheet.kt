package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.databinding.BacktestResultRowBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BacktestResultRowBottomSheet: BottomSheetDialogFragment() {
    private val args: BacktestResultRowBottomSheetArgs by navArgs()
    private lateinit var databaseViewModel: DatabaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = BacktestResultRowBottomSheetBinding.inflate(inflater, container, false)
        databaseViewModel = ViewModelProvider(requireActivity()).get(DatabaseViewModel::class.java)

        val currentItem = args.backtestResult
        binding.backtestInfo.symbolText.text = currentItem.backtestResult.backtestInput.stock.symbol
        binding.backtestInfo.strategyNameText.text = currentItem.backtestResult.backtestInput.strategyInput.strategy.strategyName
        binding.backtestInfo.dateRangeText.text = getBacktestPeriodString(currentItem.backtestResult, requireContext())

        binding.deleteRow.setOnClickListener {
            databaseViewModel.deleteBacktestResult(currentItem)
            this.dismiss()
        }
        binding.rerunRow.setOnClickListener {
            val start = currentItem.backtestResult.date.first().toCalendar()
            val end = getYesterday()
            val action = BacktestResultRowBottomSheetDirections.actionBacktestResultRowBottomSheetToRunStrategyDialog(
                start,
                end,
                arrayOf(currentItem.backtestResult.backtestInput.strategyInput),
                arrayOf(currentItem.backtestResult.backtestInput.stock),
                currentItem.backtestResult.backtestInput.transactionCost
            )
            findNavController().navigate(action)
//            this.dismiss()
        }

        binding.shareRow.setOnClickListener {
            val action = BacktestResultRowBottomSheetDirections.actionBacktestResultRowBottomSheetToShareBacktestFragment(currentItem.backtestResult)
            findNavController().navigate(action)
            this.dismiss()
        }

        return binding.root
    }
}