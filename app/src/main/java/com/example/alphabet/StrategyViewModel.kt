package com.example.alphabet

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ta4j.core.*
import org.ta4j.core.analysis.criteria.MaximumDrawdownCriterion
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.reports.TradingStatement
import org.ta4j.core.reports.TradingStatementGenerator
import org.ta4j.core.rules.StopGainRule
import yahoofinance.YahooFinance
import yahoofinance.histquotes.Interval
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class StrategyViewModel: ViewModel() {
    var stratName = MutableLiveData<String>()
    var symbol = MutableLiveData<String>()
    var start = MutableLiveData<Calendar?>()
    var end = MutableLiveData<Calendar?>()

    val series = mutableStateOf(BaseBarSeries("mySeries"))

    lateinit var entryRules: List<Rule>
    lateinit var exitRules: List<Rule>
//    var strategyInput: MutableLiveData<StrategyInput> = MutableLiveData()
    var entryRulesInput = mutableListOf<RuleInput>()
    var exitRulesInput = mutableListOf<RuleInput>()
    var stopGain: MutableLiveData<Float?> = MutableLiveData()
    var stopLoss: MutableLiveData<Float?> = MutableLiveData()

    var entryRule: MutableLiveData<Rule> = MutableLiveData()
    var exitRule: MutableLiveData<Rule> = MutableLiveData()

    var strategy: MutableLiveData<Strategy> = MutableLiveData()
    var tradingRecord: MutableLiveData<TradingRecord> = MutableLiveData()
    var tradingStatement: MutableLiveData<TradingStatement> = MutableLiveData()

    val loading = mutableStateOf(false)

//    lateinit var tradingRecord: TradingRecord

    fun loadData() {
        val stock = YahooFinance.get(symbol.value, true)
        val data = stock!!.run {
            when {
                (start.value == null && end.value == null) -> this.getHistory(Interval.DAILY)  // 1 year data
                end.value == null -> this.getHistory(start.value, Interval.DAILY)
                else -> this.getHistory(start.value, end.value, Interval.DAILY)
            }
        }
        for (row in data) {
            val date = ZonedDateTime.ofInstant(row.date.toInstant(), ZoneId.systemDefault())
            if (row.open != null && row.high != null && row.low != null && row.close != null && row.volume != null) {
                series.value!!.addBar(date, row.open, row.high, row.low, row.close, row.volume)
            }
        }
    }

    private fun updateStrategy() {
        entryRules = entryRulesInput.map { it.parseRule(series.value!!) }
        exitRules = exitRulesInput.map { it.parseRule(series.value!!) }
        val nonIndRule = mutableListOf<Rule>()
        stopGain.value?.run { StopGainRule(ClosePriceIndicator(series.value), this) }
            ?.apply { nonIndRule.add(this) }
        stopLoss.value?.run { StopGainRule(ClosePriceIndicator(series.value), this) }
            ?.apply { nonIndRule.add(this) }

        entryRule.value = aggRule(entryRules, listOf())
        exitRule.value = aggRule(exitRules, nonIndRule.toList())
        strategy.value = BaseStrategy(entryRule.value, exitRule.value)
    }

    private fun updateTradingStatement() {
        tradingStatement.value =
            TradingStatementGenerator().generate(strategy.value, tradingRecord.value, series.value)
    }

    fun entryRulesDes(): String {
        return entryRulesInput.joinToString("\n")
    }

    fun exitRulesDes(): String {
        val stopGainText = stopGain.value?.run { "\nStop Gain ($this%)" } ?: ""
        val stopLossText = stopLoss.value?.run { "\nStop Loss ($this%)" } ?: ""
        return exitRulesInput.joinToString("\n") + stopGainText + stopLossText
    }

    fun backtest() {
        viewModelScope.launch {
            loading.value = true
            withContext(Dispatchers.IO) {
                series.value = BaseBarSeries("mySeries")
//                series.postValue(BaseBarSeries("mySeries"))
                loadData()
            }
            updateStrategy()
            withContext(Dispatchers.Default) {
                tradingRecord.postValue(BarSeriesManager(series.value).run(strategy.value))
            }
            updateTradingStatement()
            loading.value = false
        }
    }

    fun metrics(): Metrics {
        val pnl = tradingStatement.value!!.performanceReport.totalProfitLoss.doubleValue()
        val pnlPct = tradingStatement.value!!.performanceReport.totalProfitLossPercentage.doubleValue()
        val profitCount = tradingStatement.value!!.positionStatsReport.profitCount.doubleValue()
        val lossCount = tradingStatement.value!!.positionStatsReport.lossCount.doubleValue()
        val nTrade = profitCount + lossCount
        val winRate = profitCount / nTrade
        val mdd = MaximumDrawdownCriterion().calculate(series.value, tradingRecord.value!!).doubleValue()
        val pnlList = tradingRecord.value!!.positions.mapIndexed { index, position ->
            val eachPnl = (position.exit.pricePerAsset - position.entry.pricePerAsset).doubleValue()
            eachPnl / position.entry.pricePerAsset.doubleValue()
        }
        return Metrics(pnl, pnlPct, profitCount, lossCount, nTrade, winRate, mdd, pnlList)
    }
}