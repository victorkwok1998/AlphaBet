package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.example.alphabet.MyApplication.Companion.sdfLong
import com.example.alphabet.components.RowItem

class BacktestDescFragment: Fragment() {
    private val viewModel: StrategyViewModel by navGraphViewModels(R.id.strategy_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent { 
                BacktestDesc()
            }
        }
    }

    @Composable
    fun BacktestDesc() {
        val stratName = viewModel.stratName.value!!
        val symbol = viewModel.symbol.value!!
        val startDate = sdfLong.format(viewModel.start.value!!.time)
        val endDate = sdfLong.format(viewModel.end.value!!.time)

        Column {
            Text(
                "$stratName Strategy",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            RowItem(heading = "Symbol", body = symbol, icon = R.drawable.ic_baseline_leaderboard_24)
            Divider(modifier = Modifier.padding(vertical = 10.dp), thickness = 1.dp)
            Row (
                Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth()
                    ){
                Column(
                    Modifier
                        .weight(1f)
                ) {
                    RowItem(heading = "Start Date", body = startDate, icon = R.drawable.ic_baseline_date_range_24)
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
                    RowItem(heading = "End Date", body = endDate, icon = R.drawable.ic_baseline_date_range_24)
                }
            }
            Divider(Modifier.padding(vertical = 10.dp), thickness = 1.dp)
            RowItem(
                heading = "Entry Rule",
                body = viewModel.entryRulesDes(),
                icon = R.drawable.ic_baseline_trending_up_24
            )
            Divider(Modifier.padding(vertical = 10.dp), thickness = 1.dp)
            RowItem(
                heading = "Exit Rule",
                body = viewModel.exitRulesDes(),
                icon = R.drawable.ic_baseline_trending_down_24
            )
            Divider(Modifier.padding(vertical = 10.dp), thickness = 1.dp)
        }
    }
}