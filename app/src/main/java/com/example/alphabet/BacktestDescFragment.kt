package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.alphabet.MyApplication.Companion.sdfLong
import com.example.alphabet.components.RowItem
import com.example.alphabet.ui.theme.*
import java.util.*

class BacktestDescFragment: Fragment() {
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
                    viewModel.symbolStrategyList.forEach { backtestInput ->
                        BackTestDesc(
                            backtestInput.strategyInput,
                            backtestInput.symbol,
                            viewModel.start.value!!,
                            viewModel.end.value!!
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }

    @Composable
    fun BackTestDesc(strategy: StrategyInput, symbol: String, start: Calendar, end: Calendar) {
        val startDate = sdfLong.format(start.time)
        val endDate = sdfLong.format(end.time)

        Column(Modifier.padding(20.dp)) {
            Text(
                "Backtest Description",
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            RowItem(heading = "Symbol", body = symbol, icon = R.drawable.ic_baseline_timeline_24)
            Divider(modifier = Modifier.padding(vertical = 10.dp), thickness = 1.dp)
            Row(
                Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .weight(1f)
                ) {
                    RowItem(heading = "Start Date", body = startDate, icon = R.drawable.ic_baseline_date_range_24, tint = Color.DarkGray)
                }
                Divider(
                    Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                Column(
                    Modifier
                        .weight(1f)
                ) {
                    RowItem(heading = "End Date", body = endDate, icon = R.drawable.ic_baseline_date_range_24, tint = Color.DarkGray)
                }
            }
            Divider(Modifier.padding(vertical = 10.dp), thickness = 1.dp)
            RowItem(
                heading = "Strategy",
                body = strategy.strategyName,
                icon = R.drawable.ic_baseline_emoji_objects_24,
                tint = amber500
            )
            Divider(Modifier.padding(vertical = 10.dp), thickness = 1.dp)
            RowItem(
                heading = "Entry Rule",
                body = strategy.entryRulesDes(),
                icon = R.drawable.ic_baseline_trending_up_24,
                tint = green500
            )
            Divider(Modifier.padding(vertical = 10.dp), thickness = 1.dp)
            RowItem(
                heading = "Exit Rule",
                body = strategy.exitRulesDes(),
                icon = R.drawable.ic_baseline_trending_down_24,
                tint = red500
            )
            // Divider(Modifier.padding(vertical = 10.dp), thickness = 1.dp)
        }
    }
}