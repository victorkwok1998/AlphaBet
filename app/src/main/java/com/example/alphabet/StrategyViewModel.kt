package com.example.alphabet

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.ta4j.core.*
import org.ta4j.core.analysis.criteria.MaximumDrawdownCriterion
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.reports.TradingStatement
import org.ta4j.core.reports.TradingStatementGenerator
import org.ta4j.core.rules.StopGainRule
import yahoofinance.YahooFinance
import yahoofinance.histquotes.Interval
import java.time.Year
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class StrategyViewModel: ViewModel() {
    private val defaultEnd = Calendar.getInstance().apply { add(Calendar.DATE, -1) }  // yesterday
    private val defaultStart = Calendar.getInstance().apply {
        time = defaultEnd.time
        add(Calendar.YEAR, -1)
    } // one year before
    private val EMPTY_STRATEGY = StrategyInput("EMPTY", "", mutableListOf(), mutableListOf(), "", "")

    val start = mutableStateOf(defaultStart)
    val end = mutableStateOf(defaultEnd)

//    val selectToEditStrategy = mutableStateOf(0)
    val inputToSelectStrategy = mutableStateOf(0)

//    val symbolStrategyList = mutableStateListOf(Pair(mutableStateOf(""), mutableStateOf(-1)))
    val symbolStrategyList = mutableStateListOf(BacktestInput(mutableStateOf(""), EMPTY_STRATEGY))
//    val stratIdMap = mutableStateOf(listOf<Pair<String, StrategyInput>>())
    val seriesMap = mutableMapOf<String, BaseBarSeries>()

    var metrics = mutableStateOf(listOf<Metrics>())

    fun reset() {
        start.value = defaultStart
        end.value = defaultEnd
//        selectToEditStrategy.value = 0
        inputToSelectStrategy.value = 0
        symbolStrategyList.clear()
        addEmpty()
        seriesMap.clear()
        metrics.value = listOf()
    }

    fun loadData() {
        seriesMap.clear()
        symbolStrategyList.forEach { it.symbol.value = it.symbol.value.uppercase() }
        val symbols = symbolStrategyList.map { it.symbol.value }.toSet()

        symbols.forEach { s ->
            val series = BaseBarSeries(s)
            YahooFinance.get(s, true)
                .run { getHistory(start.value, end.value, Interval.DAILY) }
                .forEach { row ->
                    val date = ZonedDateTime.ofInstant(row.date.toInstant(), ZoneId.systemDefault())
                    if (row.open != null && row.high != null && row.low != null && row.close != null && row.volume != null)
                        series.addBar(date, row.open, row.high, row.low, row.close, row.volume)
                }
            seriesMap[s] = series
        }
    }

    fun runStrategy() {
        metrics.value = symbolStrategyList.map {
            val series = seriesMap[it.symbol.value]!!
            val strategy = it.strategyInput.toStrategy(series)
            val tradingRecord = BarSeriesManager(series).run(strategy)
            val tradingStatement = TradingStatementGenerator().generate(strategy, tradingRecord, series)

            val pnl = tradingStatement.performanceReport.totalProfitLoss.doubleValue()
            val pnlPct = tradingStatement.performanceReport.totalProfitLossPercentage.doubleValue()
            val profitCount = tradingStatement.positionStatsReport.profitCount.doubleValue()
            val lossCount = tradingStatement.positionStatsReport.lossCount.doubleValue()
            val nTrade = profitCount + lossCount
            val winRate = profitCount / nTrade
            val mdd = MaximumDrawdownCriterion().calculate(series, tradingRecord).doubleValue()
            val pnlList = tradingRecord.positions.mapIndexed { index, position ->
                val eachPnl = (position.exit.pricePerAsset - position.entry.pricePerAsset).doubleValue()
                eachPnl / position.entry.pricePerAsset.doubleValue()
            }
            Metrics(
                tradingRecord, pnl, pnlPct, profitCount, lossCount, nTrade,
                winRate, mdd, pnlList
            )
        }
    }

    fun symbolStrategyStringList(): List<String> {
        return symbolStrategyList.map { "${it.symbol.value}, ${it.strategyInput.strategyName}" }
    }

    fun setDateRange(year: Int) {
        end.value = defaultEnd
        start.value = Calendar.getInstance().apply {
            time = defaultEnd.time
            add(Calendar.YEAR, -year)
        }
    }

    fun addEmpty() {
        symbolStrategyList.add(BacktestInput(mutableStateOf(""), EMPTY_STRATEGY))
    }

//    fun updateStrategy() {
//        entryRules.value = entryRulesInput.map { it.parseRule(series.value) }
//        exitRules.value = exitRulesInput.map { it.parseRule(series.value) }
//        val nonIndRule = mutableListOf<Rule>()
//        stopGain.value?.run { StopGainRule(ClosePriceIndicator(series.value), this) }
//            ?.apply { nonIndRule.add(this) }
//        stopLoss.value?.run { StopGainRule(ClosePriceIndicator(series.value), this) }
//            ?.apply { nonIndRule.add(this) }
//
//        entryRule.value = aggRule(entryRules.value, listOf())
//        exitRule.value = aggRule(exitRules.value, nonIndRule.toList())
//        strategy.value = BaseStrategy(entryRule.value, exitRule.value)
//    }

//    fun updateTradingStatement() {
//        tradingStatement.postValue(
//            TradingStatementGenerator().generate(strategy.value, tradingRecord.value, series.value)
//        )
//    }

//    fun backtest() {
//        series.value = BaseBarSeries("mySeries")
//        loadData()
//        updateStrategy()
//        tradingRecord.postValue(BarSeriesManager(series.value).run(strategy.value))
//        updateTradingStatement()
//    }

//    fun metrics(): Metrics {
//        val pnl = tradingStatement.value!!.performanceReport.totalProfitLoss.doubleValue()
//        val pnlPct = tradingStatement.value!!.performanceReport.totalProfitLossPercentage.doubleValue()
//        val profitCount = tradingStatement.value!!.positionStatsReport.profitCount.doubleValue()
//        val lossCount = tradingStatement.value!!.positionStatsReport.lossCount.doubleValue()
//        val nTrade = profitCount + lossCount
//        val winRate = profitCount / nTrade
//        val mdd = MaximumDrawdownCriterion().calculate(series.value, tradingRecord.value!!).doubleValue()
//        val pnlList = tradingRecord.value!!.positions.mapIndexed { index, position ->
//            val eachPnl = (position.exit.pricePerAsset - position.entry.pricePerAsset).doubleValue()
//            eachPnl / position.entry.pricePerAsset.doubleValue()
//        }
//        return Metrics(pnl, pnlPct, profitCount, lossCount, nTrade, winRate, mdd, pnlList)
//    }
}