package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.navGraphViewModels
import com.example.alphabet.MyApplication.Companion.dec
import com.example.alphabet.MyApplication.Companion.pct
import com.example.alphabet.components.SimpleTable
import com.github.mikephil.charting.charts.CombinedChart
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.TradingRecord

class BacktestTradesFragment : Fragment() {
    private val viewModel: StrategyViewModel by activityViewModels()
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                Column(
                    Modifier.verticalScroll(rememberScrollState())
                ) {
                    val symbolStrategyList = viewModel.symbolStrategyStringList()
                    viewModel.metrics.value.forEachIndexed { index, it ->
                        TradeTable(
                            symbolStrategy = symbolStrategyList[index],
                            series = viewModel.seriesMap[viewModel.symbolStrategyList[index].symbol]!!,
                            tradingRecord = it.tradingRecord
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun TradeTable(symbolStrategy: String, series: BaseBarSeries, tradingRecord: TradingRecord) {
        val header = listOf("No.", "Entry Price", "Exit Price", "PnL")
        val weights = listOf(1f, 2f, 2f, 1.2f)
        val data = tradingRecord.positions.mapIndexed { index, position ->
            listOf(
                (index + 1).toString(),
                position.entry.pricePerAsset.let { dec.format(it.floatValue()) },
                position.exit.pricePerAsset.let { dec.format(it.floatValue()) },
                (position.exit.pricePerAsset.floatValue() / position.entry.pricePerAsset.floatValue() - 1).let { pct.format(it) }
            )
        }
        Column(
            Modifier
                .padding(20.dp)
        ) {
//            Text(text = "All Trades", style = MaterialTheme.typography.h5)
            Text(text = "Trades of $symbolStrategy", style = MaterialTheme.typography.h6, modifier = Modifier.padding(vertical = 10.dp))
            AndroidView(
                ::CombinedChart,
                Modifier
                    .height(350.dp)
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) { chart ->
                plotTrades(chart, series, tradingRecord,
                    symbolStrategy, requireContext())
            }
            SimpleTable(header = header, data = data, weights = weights)
        }
    }
}