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
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.activityViewModels
import com.example.alphabet.components.MyCard
import com.example.alphabet.components.MyLegend
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
                val seriesTradingRecord = viewModel.symbolStrategyList
                    .zip(viewModel.metrics.value)
                    .map { (backtestInput, metric) ->
                    viewModel.seriesMap[backtestInput.symbol]!! to metric.tradingRecord
                }
                val symbolStrategyList = viewModel.symbolStrategyStringList()
                val metricsList = viewModel.metrics.value
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
            MetricsCard(metricsList, symbolStrategyList)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    @Composable
    fun EquityCurvePlotCard(
        seriesTradingRecord: List<Pair<BaseBarSeries, TradingRecord>>,
        labels: List<String>,
    ) {
        val enabledLines = labels.map { mutableStateOf(true) }
        MyCard {
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
                        enabledLines.map { it.value },
                        requireContext()
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                // Legend
                MyLegend(labels, enabledLines)
            }
        }
    }

    @Composable
    fun SummaryCard(metricsList: List<Metrics>, symbolStrategyList: List<String>) {
        MyCard {
            Column(Modifier.padding(20.dp)) {
                Text("Summary", style = MaterialTheme.typography.h6)
                RadarChartPlot(metricsList, labels = symbolStrategyList)
            }
        }
    }

    @Composable
    fun MetricsCard(metricsList: List<Metrics>, symbolStrategyList: List<String>) {
        MyCard {
            Column(Modifier.padding(20.dp)) {
                Text("Metrics", style = MaterialTheme.typography.h6)
                MetricsTable(metricsList, symbolStrategyList)
            }
        }
    }

    @Composable
    fun RadarChartPlot(metricsList: List<Metrics>, labels: List<String>) {
        val criterion = resources.getStringArray(R.array.radar_label).toList()
        // val metrics = listOf(pnlPct, -mdd, nTrade, pnlList.average(), winRate)
        val scores = metricsList
            .map { listOf(it.pnlPct, -it.mdd, it.nTrade, it.pnlList.average(), it.winRate) }
            .map { metrics ->
                List(criterion.size) { i ->
                    val k = criterion[i]
                    val v = metrics[i]
                    val score =
                        staticDataViewModel.radarChartRange.value[k]!!.binarySearch(v.toFloat())
                    when {
                        score < 0 -> -score - 1
                        else -> score
                    }.toFloat()
                }
            }
        val enabledLines = labels.map { mutableStateOf(true) }
        Column {
            AndroidView(
                ::RadarChart,
                Modifier
                    .height(300.dp)
                    .fillMaxWidth()
            ) { radarPlot ->
                plotRadarChart(radarPlot, scores, criterion,enabledLines.map{it.value}, requireContext())
            }
            Spacer(modifier = Modifier.height(20.dp))
            // Legend
            MyLegend(labels, enabledLines)
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

        StackedTable(primaryIndex, symbolStrategyList, data)
    }
}