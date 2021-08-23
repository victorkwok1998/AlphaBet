package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.example.alphabet.databinding.FragmentBacktestTradesBinding

class BacktestTradesFragment : Fragment(R.layout.fragment_backtest_trades) {
    private var _binding: FragmentBacktestTradesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StrategyViewModel by navGraphViewModels(R.id.strategy_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBacktestTradesBinding.inflate(inflater, container, false)
        val tradingRecord = viewModel.tradingRecord.value!!
        tradingRecord.positions.forEachIndexed { index, position ->
            val row = layoutInflater.inflate(R.layout.trade_table_row, null)
            row.findViewById<TextView>(R.id.trade_id_text).text = (index + 1).toString()
            row.findViewById<TextView>(R.id.entry_price_text).text = position.entry.pricePerAsset.toString()
            row.findViewById<TextView>(R.id.exit_price_text).text = position.exit.pricePerAsset.toString()
            row.findViewById<TextView>(R.id.pnl_text).text = (position.exit.pricePerAsset - position.entry.pricePerAsset).toString()
            binding.tradeTable.addView(row)
        }
        return binding.root
    }
}