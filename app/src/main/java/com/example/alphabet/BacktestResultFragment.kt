package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import com.example.alphabet.MyApplication.Companion.dec
import com.example.alphabet.MyApplication.Companion.intFormat
import com.example.alphabet.MyApplication.Companion.pct
import com.example.alphabet.databinding.FragmentBacktestResultBinding
import com.example.alphabet.MyApplication.Companion.sdfLong
import com.example.alphabet.components.CircularIndeterminateProgressBar
import com.example.alphabet.components.RowItem
import com.example.alphabet.components.SimpleTable
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.RadarChart
import kotlinx.coroutines.launch
import org.ta4j.core.analysis.criteria.MaximumDrawdownCriterion
import java.text.DecimalFormat
import kotlin.math.abs

class BacktestResultFragment: Fragment(R.layout.fragment_backtest_result) {
    private var _binding: FragmentBacktestResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StrategyViewModel by navGraphViewModels(R.id.strategy_nav_graph)
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.backtest()
        return ComposeView(requireContext()).apply {
            setContent {
                val loading = viewModel.loading.value
                val listState = rememberLazyListState()
                val titles = listOf("PERF", "TRADES", "DESC")
                val coroutineScope = rememberCoroutineScope()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Strategy Report") }
                        )
                    }
                ){
                    Column {
                        CircularIndeterminateProgressBar(
                            isDisplayed = loading,
                            text = stringResource(id = R.string.trading_result_progress_loading_data)
                        )
                        if (!loading) {
                            TabRow(selectedTabIndex = listState.firstVisibleItemIndex) {
                                titles.forEachIndexed { index, title ->
                                    Tab(
                                        text = { Text(title) },
                                        selected = listState.firstVisibleItemIndex == index,
                                        onClick = {
                                            coroutineScope.launch {
                                                listState.animateScrollToItem(index = index)
                                            }
                                        }
                                    )
                                }
                            }
                            LazyColumn(
                                state = listState
                            ) {
                                item {
                                    BackTestSummary()
                                    Divider(Modifier.padding(vertical = 20.dp), thickness = 10.dp)
                                }
                                item {
                                    TradeTable()
                                    Divider(Modifier.padding(vertical = 20.dp), thickness = 10.dp)
                                }
                                item {
                                    BacktestDesc()
                                    Spacer(Modifier.height(200.dp))
                                }
                            }
                        }
                    }
                }
            }
        }



//        _binding = FragmentBacktestResultBinding.inflate(inflater, container, false)
//        lifecycleScope.launch() {
//            binding.tradeResultLayout.visibility = View.INVISIBLE
//            binding.tradingResultProgressText.text = resources.getText(R.string.trading_result_progress_loading_data)
//            withContext(Dispatchers.IO) {
//                viewModel.series.postValue(BaseBarSeries("mySeries"))  // Clear data before loading
//                viewModel.loadData()
//            }
////            if (viewModel.series.value!!.isEmpty) {
////                binding.tradingResultProgressText.text = resources.getText(R.string.trading_result_progress_loading_data)
////                withContext(Dispatchers.IO) {
////                    viewModel.loadData()
////                }
////            }
//
//            binding.tradingResultProgressText.text = resources.getText(R.string.trading_result_progress_running_strategy)
//            viewModel.updateStrategy()
//            withContext(Dispatchers.Default) {
//                viewModel.tradingRecord.postValue(BarSeriesManager(viewModel.series.value).run(viewModel.strategy.value))
//            }
//            // Number format
//            val dec = DecimalFormat("#,###0.00")
//            val intFormat = DecimalFormat("#,##0")
//            val pct = DecimalFormat("#,###0.00%")
//            // metrics
//            viewModel.updateTradingStatement()
//            val pnlPct = viewModel.tradingStatement.value!!.performanceReport.totalProfitLossPercentage.doubleValue()
//            val profitCount = viewModel.tradingStatement.value!!.positionStatsReport.profitCount.doubleValue()
//            val lossCount = viewModel.tradingStatement.value!!.positionStatsReport.lossCount.doubleValue()
//            val nTrade = profitCount + lossCount
//            val winRate = profitCount / nTrade
//            val mdd = MaximumDrawdownCriterion().calculate(viewModel.series.value!!, viewModel.tradingRecord.value!!).doubleValue()
//
//            val tabLayout = binding.tabLayout
//            val viewPagerAdapter = ViewPagerAdapter(parentFragmentManager, lifecycle)
//            val viewPager2 = binding.viewPager2
//                .apply { adapter =  viewPagerAdapter}
//                .apply {
//                    registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
//                        override fun onPageSelected(position: Int) {
//                            tabLayout.selectTab(tabLayout.getTabAt(position))
//                        }
//                    })
//                }
//            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabSelected(tab: TabLayout.Tab) {
//                    viewPager2.currentItem = tab.position
//                }
//
//                override fun onTabUnselected(tab: TabLayout.Tab?) {
//
//                }
//
//                override fun onTabReselected(tab: TabLayout.Tab?) {
//
//                }
//            })
//
////            // trade summary
////            viewModel.tradingStatement.value?.apply {
////                val pnlString = performanceReport.totalProfitLoss.doubleValue().let { dec.format(it) }
////                val pnlPctString = pnlPct.let { dec.format(it) }
////                binding.totalPnlText.text = "${pnlString} (${pnlPctString}%)"
////                binding.winningTradesText.text = profitCount.let { intFormat.format(it) }
////                binding.losingTradesText.text = lossCount.let { intFormat.format(it) }
////            }
////            binding.mddText.text = mdd.let { pct.format(it) }
////
////            // trade table
////            //TODO
////            val pnlList = mutableListOf<Double>()//TODO
////            viewModel.tradingRecord.value!!.positions.forEachIndexed { index, position ->
////                val tradeTableRowBinding = TradeTableRowBinding.inflate(layoutInflater, null, false)
////                with (tradeTableRowBinding) {
////                    this.tradeIdText.text = (index + 1).toString()
////                    this.entryPriceText.text = position.entry.pricePerAsset.doubleValue().run { dec.format(this) }
////                    this.exitPriceText.text = position.exit.pricePerAsset.doubleValue().run { dec.format(this) }
////                    val pnl = (position.exit.pricePerAsset - position.entry.pricePerAsset).doubleValue()
////                    pnlList.add(pnl / position.entry.pricePerAsset.doubleValue())//TODO
////                    this.pnlText.text = pnl.run { dec.format(this) }
////                    binding.tradeTable.addView(this.root)
////                }
////            }
//            with(binding){
//                tradingResultProgress.visibility = View.GONE
//                tradeResultLayout.visibility = View.VISIBLE
////
////                symbolResult.text = SpannableStringBuilder()
////                    .bold { append("Symbol: ") }
////                    .append(viewModel.symbol.value)
////                startDateResult.text = SpannableStringBuilder()
////                    .bold { append("Start Date: ") }
////                    .append(sdfLong.format(viewModel.start.value!!.time))
////                endDateResult.text = SpannableStringBuilder()
////                    .bold { append("End Date: ") }
////                    .append(sdfLong.format(viewModel.end.value!!.time))
////
////                entryStrategyText.text = viewModel.entryRulesDes()
////                exitStrategyText.text = viewModel.exitRulesDes()
////                buyAndHoldCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
////                    if (isChecked)
////                        plotStrategyVsBuyAndHold(equityCurvePlot, viewModel.series.value!!, viewModel.tradingRecord.value!!, viewModel.symbol.value, requireContext())
////                    else
////                        plotEquityCurveFromCashFlow(binding.equityCurvePlot, viewModel.series.value!!, viewModel.tradingRecord.value!!, viewModel.symbol.value)
////                }
////                // Radar Chart
////                val labels = resources.getStringArray(R.array.radar_label).toList()
////                val metrics = listOf(pnlPct, -mdd, nTrade, pnlList.average(), winRate)
////                val scores = List(labels.size) { i ->
////                    val k = labels[i]
////                    val v = metrics[i]
////                    abs(staticDataViewModel.radarChartRange.value!![k]!!.binarySearch(v.toFloat()) + 1f)
////                }
////                plotRadarChart(radarPlot, scores, labels)
//            }
////            plotEquityCurveFromCashFlow(binding.equityCurvePlot, viewModel.series.value!!, viewModel.tradingRecord.value!!, viewModel.symbol.value)
//        }
//
//
////        tradingRecord.positions.forEachIndexed { index, position ->
////            val row = layoutInflater.inflate(R.layout.trade_table_row, null)
////            row.findViewById<TextView>(R.id.trade_id_text).text = (index + 1).toString()
////            row.findViewById<TextView>(R.id.entry_price_text).text = position.entry.pricePerAsset.toString()
////            row.findViewById<TextView>(R.id.exit_price_text).text = position.exit.pricePerAsset.toString()
////            row.findViewById<TextView>(R.id.pnl_text).text = (position.exit.pricePerAsset - position.entry.pricePerAsset).toString()
////            binding.tradeTable.addView(row)
////        }
//        binding.optAppBar.setOnMenuItemClickListener{
//            when(it.itemId) {
//                R.id.action_fav_strat -> {
//                    val cashFlow = getCashFlow(viewModel.series.value!!, viewModel.tradingRecord.value!!)
//                    val date = List(viewModel.series.value!!.barCount) {i ->
//                        Date.from(viewModel.series.value!!.getBar(i).endTime.toInstant())
//                            .run { sdfISO.format(this) }
//                    }
//                    val backtestResultString = Json.encodeToString(BacktestResult(
//                        viewModel.stratName.value!!,
//                        viewModel.symbol.value!!,
//                        date,
//                        cashFlow
//                    ))
//                    File(requireContext().filesDir, "myStrategy.json")
//                        .writeText(backtestResultString)
//                    Toast.makeText(context, requireContext().getString(R.string.set_fav), Toast.LENGTH_LONG).show()
//                    true
//                }
//                else -> false
//            }
//        }
//
////        binding.backToHome.setOnClickListener {
////            val action = BacktestResultFragmentDirections.actionGlobalHomeFragment()
////            findNavController().navigate(action)
////        }
//        return binding.root
    }
    @Composable
    fun BacktestDesc() {
        val stratName = viewModel.stratName.value!!
        val symbol = viewModel.symbol.value!!
        val startDate = sdfLong.format(viewModel.start.value!!.time)
        val endDate = sdfLong.format(viewModel.end.value!!.time)

        Column(Modifier.padding(horizontal = 20.dp)) {
            Text(
                "Strategy Description",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(vertical = 15.dp)
            )
            RowItem(heading = "Symbol", body = symbol, icon = R.drawable.ic_baseline_leaderboard_24)
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
                heading = "Strategy",
                body = "$stratName Strategy",
                icon = R.drawable.ic_baseline_emoji_objects_24
            )
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
            // Divider(Modifier.padding(vertical = 10.dp), thickness = 1.dp)
        }
    }

    @Composable
    fun EquityCurveScreen() {
        Column(Modifier.padding(20.dp)) {
            Text(text = "Equity Curve", style = MaterialTheme.typography.h5)
        }
    }
    
    @Composable
    fun TradeTable() {
        val header = listOf("No.", "Entry Price", "Exit Price", "PnL")
        val weights = listOf(1f, 2f, 2f, 1.2f)
        val data = viewModel.tradingRecord.value!!.positions.mapIndexed { index, position ->
            listOf(
                (index + 1).toString(),
                position.entry.pricePerAsset.let { dec.format(it.floatValue()) },
                position.exit.pricePerAsset.let { dec.format(it.floatValue()) },
                (position.exit.pricePerAsset - position.entry.pricePerAsset).let { dec.format(it.floatValue()) }
            )
        }
        Column(Modifier.padding(20.dp)) {
            Text(text = "All Trades", style = MaterialTheme.typography.h5)
            Text(text = viewModel.symbol.value!!, style = MaterialTheme.typography.h6, modifier = Modifier.padding(vertical = 10.dp))
            AndroidView(::CombinedChart,
                Modifier
                    .height(350.dp)
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) { chart ->
                plotTrades(chart, viewModel.series.value, viewModel.tradingRecord.value!!,
                    viewModel.symbol.value, requireContext())
            }
            SimpleTable(header = header, data = data, weights = weights)
        }
    }

    @Composable
    fun BackTestSummary() {
        val (pnl, pnlPct, profitCount, lossCount, nTrade, winRate, mdd, pnlList) = viewModel.metrics()

        Column(Modifier.padding(20.dp)) {
            Text(text = "Strategy Performance", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            Text(text = "Hypothetical growth of \$1", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            // Equity Curve
            AndroidView(::LineChart,
                Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            ) { v ->
                plotEquityCurveFromCashFlow(
                    v,
                    viewModel.series.value,
                    viewModel.tradingRecord.value!!,
                    viewModel.symbol.value,
                    requireContext()
                )
            }
            Text("Summary", style = MaterialTheme.typography.h6, modifier = Modifier.padding(vertical = 20.dp))
            // Radar Chart
            AndroidView(::RadarChart,
                Modifier
                    .height(300.dp)
                    .fillMaxWidth()
            ) { radarPlot ->
                val labels = resources.getStringArray(R.array.radar_label).toList()
                val metrics = listOf(pnlPct, -mdd, nTrade, pnlList.average(), winRate)
                val scores = List(labels.size) { i ->
                    val k = labels[i]
                    val v = metrics[i]
                    val score = staticDataViewModel.radarChartRange.value!![k]!!.binarySearch(v.toFloat())
                    when {
                        score < 0 -> -score - 1
                        else -> score
                    }.toFloat()
                }
                plotRadarChart(radarPlot, scores, labels)
            }
            val data = listOf(
                listOf("Total PnL", "${dec.format(pnl)} (${dec.format(pnlPct)})%"),
                listOf("Maximum Drawdown", pct.format(mdd)),
                listOf("Winning Trades", intFormat.format(profitCount)),
                listOf("Losing Trades", intFormat.format(lossCount))
            )
            SimpleTable(header = listOf("Metrics", ""), data = data)
        }
    }
}