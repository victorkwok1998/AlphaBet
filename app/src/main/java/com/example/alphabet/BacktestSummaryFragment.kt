package com.example.alphabet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.navGraphViewModels
import com.example.alphabet.databinding.FragmentBacktestSummaryBinding
import org.ta4j.core.analysis.criteria.MaximumDrawdownCriterion
import java.text.DecimalFormat
import kotlin.math.abs


class BacktestSummaryFragment : Fragment(R.layout.fragment_backtest_summary) {
    private var _binding: FragmentBacktestSummaryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StrategyViewModel by navGraphViewModels(R.id.strategy_nav_graph)
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBacktestSummaryBinding.inflate(inflater, container, false)

        // Number format
        val dec = DecimalFormat("#,###0.00")
        val intFormat = DecimalFormat("#,##0")
        val pct = DecimalFormat("#,###0.00%")

        val pnlPct = viewModel.tradingStatement.value!!.performanceReport.totalProfitLossPercentage.doubleValue()
        val profitCount = viewModel.tradingStatement.value!!.positionStatsReport.profitCount.doubleValue()
        val lossCount = viewModel.tradingStatement.value!!.positionStatsReport.lossCount.doubleValue()
        val nTrade = profitCount + lossCount
        val winRate = profitCount / nTrade
        val mdd = MaximumDrawdownCriterion().calculate(viewModel.series.value!!, viewModel.tradingRecord.value!!).doubleValue()
        // trade summary
        viewModel.tradingStatement.value!!.apply {
            val pnlString = performanceReport.totalProfitLoss.doubleValue().let { dec.format(it) }
            val pnlPctString = pnlPct.let { dec.format(it) }
            binding.totalPnlText.text = "${pnlString} (${pnlPctString}%)"
            binding.winningTradesText.text = profitCount.let { intFormat.format(it) }
            binding.losingTradesText.text = lossCount.let { intFormat.format(it) }
        }
        binding.mddText.text = mdd.let { pct.format(it) }

        // Generate pnlList
        val pnlList = mutableListOf<Double>()//TODO
        viewModel.tradingRecord.value!!.positions.forEachIndexed { index, position ->
                val pnl = (position.exit.pricePerAsset - position.entry.pricePerAsset).doubleValue()
                pnlList.add(pnl / position.entry.pricePerAsset.doubleValue())//TODO
            }

        // Radar Chart
        val labels = resources.getStringArray(R.array.radar_label).toList()
        val metrics = listOf(pnlPct, -mdd, nTrade, pnlList.average(), winRate)
        val scores = List(labels.size) { i ->
            val k = labels[i]
            val v = metrics[i]
            abs(staticDataViewModel.radarChartRange.value!![k]!!.binarySearch(v.toFloat()) + 1f)
        }
        plotRadarChart(binding.radarPlot, scores, labels)
        return binding.root
    }
}