package com.example.alphabet

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alphabet.database.StrategySchema
import org.ta4j.core.BarSeriesManager
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.analysis.criteria.MaximumDrawdownCriterion
import org.ta4j.core.reports.TradingStatementGenerator
import yahoofinance.YahooFinance
import yahoofinance.histquotes.Interval
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class StrategyViewModel: ViewModel() {
    val defaultEnd = Calendar.getInstance().apply { add(Calendar.DATE, -1) }  // yesterday
    val defaultStart = Calendar.getInstance().apply {
        time = defaultEnd.time
        add(Calendar.YEAR, -1)
    } // one year before
    private val EMPTY_STRATEGY = StrategyInput("", "", mutableListOf(), mutableListOf())
//    private val EMPTY_INDICATOR = IndicatorInput(IndType.INDICATOR, "", mutableListOf())
//    private val EMPTY_RULE = RuleInput(
//        EMPTY_INDICATOR.copy(),
//        EMPTY_INDICATOR.copy(),
//        Cond.CROSS_UP
//    )

    val start = MutableLiveData(defaultStart)
    val end = MutableLiveData(defaultEnd)

//    val symbolStrategyList = mutableStateListOf(BacktestInput(mutableStateOf(""), EMPTY_STRATEGY))
    val strategyList = MutableLiveData(listOf<StrategySchema>())

    val seriesMap = mutableMapOf<String, BaseBarSeries>()

    // Create Strategy
    val customStrategy = EMPTY_STRATEGY.copy("Custom Strategy", "")
//    val selectedRule = mutableStateOf(EMPTY_RULE)

    // create rule
    val primaryInd by lazy { MutableLiveData(IndicatorInput.EMPTY_INDICATOR) }
    val secondaryInd by lazy { MutableLiveData<IndicatorInput>(IndicatorInput.EMPTY_INDICATOR) }
    val cond by lazy {MutableLiveData<Cond>(Cond.CROSS_UP) }



    fun reset() {
        start.value = defaultStart
        end.value = defaultEnd
//        selectToEditStrategy.value = 0
//        inputToSelectStrategy.value = 0
        strategyList.value = emptyList()
//        addEmptyStrategy()
        seriesMap.clear()
    }

    fun setDateRange(year: Int) {
        end.value = defaultEnd
        start.value = Calendar.getInstance().apply {
            time = defaultEnd.time
            add(Calendar.YEAR, -year)
        }
    }

//    fun addEmptyStrategy() {
//        symbolStrategyList.add(BacktestInput("", EMPTY_STRATEGY))
//    }

    fun clearEntryRule() {
        primaryInd.value = IndicatorInput.EMPTY_INDICATOR
        secondaryInd.value = IndicatorInput.EMPTY_INDICATOR
        cond.value = Cond.CROSS_UP
    }

//    fun addEmptyEntryRule() {
//        val tmp = EMPTY_RULE.copy()
//        customStrategy.value.entryRulesInput.add(tmp)
//        selectedRule.value = tmp
//    }
//
//    fun addEmptyExitRule() {
//        val tmp = EMPTY_RULE.copy()
//        customStrategy.value.exitRulesInput.add(tmp)
//        selectedRule.value = tmp
//    }

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