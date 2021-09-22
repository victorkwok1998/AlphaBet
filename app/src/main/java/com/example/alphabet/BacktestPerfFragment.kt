package com.example.alphabet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.activityViewModels
import androidx.navigation.navGraphViewModels
import com.example.alphabet.components.StackedTable
import com.example.alphabet.ui.theme.grayBackground
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.RadarChart
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.TradingRecord


class BacktestPerfFragment : Fragment() {
    private val viewModel: StrategyViewModel by activityViewModels()
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val seriesTradingRecord = viewModel.metrics.value.map { viewModel.seriesMap[it.first.first]!! to it.second.tradingRecord }
                val symbolStrategyList = viewModel.symbolStrategyStringList()
                val metricsList = viewModel.metrics.value.map { it.second }
                BackTestPerf(seriesTradingRecord, metricsList, symbolStrategyList)
            }
        }
    }

    @Composable
    fun BackTestPerf(
        seriesTradingRecord: List<Pair<BaseBarSeries, TradingRecord>>,
        metricsList: List<Metrics>,
        symbolStrategyList: List<String>
    ) {
        Column(
            Modifier
                .background(grayBackground)
                .verticalScroll(rememberScrollState())
        ) {
//            Spacer(modifier = Modifier.height(10.dp))
            EquityCurvePlotCard(seriesTradingRecord, symbolStrategyList)
            Spacer(modifier = Modifier.height(10.dp))
            SummaryCard(
                metricsList,
                symbolStrategyList
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    @Composable
    fun EquityCurvePlotCard(seriesTradingRecord: List<Pair<BaseBarSeries, TradingRecord>>, labels: List<String?>) {
        Card(
            shape = RoundedCornerShape(20.dp)) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(text = "Hypothetical growth of \$1", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(10.dp))
                // Equity Curve
                AndroidView(
                    ::LineChart,
                    Modifier
                        .height(250.dp)
                        .fillMaxWidth()
                ) { v ->
                    plotEquityCurveFromCashFlow(
                        v,
                        seriesTradingRecord,
                        labels,
                        requireContext()
                    )
                }
            }
        }
    }

    @Composable
    fun SummaryCard(metricsList: List<Metrics>, symbolStrategyList: List<String>) {
        Card(shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(20.dp)) {
                Text("Summary", style = MaterialTheme.typography.h6)
                RadarChartPlot()
                Spacer(modifier = Modifier.height(10.dp))
                MetricsTable(metricsList, symbolStrategyList)
            }
        }
    }

    @Composable
    fun RadarChartPlot() {
        Column {
            AndroidView(
                ::RadarChart,
                Modifier
                    .height(300.dp)
                    .fillMaxWidth()
            ) { radarPlot ->
                val labels = resources.getStringArray(R.array.radar_label).toList()
                //                    val metrics = listOf(pnlPct, -mdd, nTrade, pnlList.average(), winRate)
                val scores = viewModel.metrics.value
                    .map { it.second }
                    .map { listOf(it.pnlPct, -it.mdd, it.nTrade, it.pnlList.average(), it.winRate) }
                    .map { metrics ->
                        List(labels.size) { i ->
                            val k = labels[i]
                            val v = metrics[i]
                            val score =
                                staticDataViewModel.radarChartRange.value[k]!!.binarySearch(v.toFloat())
                            when {
                                score < 0 -> -score - 1
                                else -> score
                            }.toFloat()
                        }
                    }
                plotRadarChart(radarPlot, scores, labels, requireContext())
            }
        }
    }

    @Composable
    fun MetricsTable(metricsList: List<Metrics>, symbolStrategyList: List<String>) {
        val primaryIndex = listOf("Return", "Maximum Drawdown", "Winning Trades", "Losing Trades")
        val data = listOf(
            metricsList.map { "${MyApplication.dec.format(it.pnlPct)}%" },
            metricsList.map { MyApplication.pct.format(it.mdd) },
            metricsList.map { MyApplication.intFormat.format(it.profitCount) },
            metricsList.map { MyApplication.intFormat.format(it.lossCount) }
        )
        
        Text(text = "Metrics", style = MaterialTheme.typography.h6)
        StackedTable(primaryIndex, symbolStrategyList, data)
    }
}