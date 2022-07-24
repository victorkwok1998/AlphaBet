package com.example.alphabet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.ta4j.core.Trade
import kotlin.math.absoluteValue

@Parcelize
@Serializable
class BacktestResult(
    val backtestInput: BacktestInput,
    val date: List<String>,
    val positionList: List<PositionData>,
    val adjCloseList: List<Float>,
): Parcelable {
    fun getCashFlow(): List<Float> {
        var currPos = 0
        var entryIndex = 0
        var exitIndex = 0
        var side = 1
        val cf = MutableList(adjCloseList.size) { 1f } // Assume start at $1

        for (i in 1 until adjCloseList.size) {
            if (currPos < positionList.size) {
                entryIndex = positionList[currPos].entry.index
                exitIndex = positionList[currPos].exit.index
                side = if (positionList[currPos].startingType == Trade.TradeType.BUY) 1 else -1
            }
            if (i > entryIndex && i <= exitIndex) {
                cf[i] =
                    (adjCloseList[i] - adjCloseList[i - 1]) / adjCloseList[entryIndex] * side + cf[i - 1]
            } else {
                cf[i] = cf[i - 1]
            }
            if (i == exitIndex) {
                currPos++
            }
        }
        return cf
    }

    fun getMetrics(): Metrics {
        val cashFlow = getCashFlow()
        val pnlList = positionList.map { it.getPnl() }
        val profitCount = pnlList.count { it > 0 }
        val lossCount = pnlList.count { it < 0 }
        val nTrade = positionList.size
        val winRate = if(nTrade == 0) 0f else profitCount.toFloat() / nTrade
        val mdd = maxDrawDown(cashFlow)
        val pnl = pnlList.sum()
        val pnlPct = (cashFlow.last() / cashFlow.first() - 1)

        val profitFactor = if(pnlList.isEmpty()) 0f else pnlList.filter { it > 0 }.sum() / pnlList.filter { it < 0 }.sum().absoluteValue
        return Metrics(pnl, pnlPct, profitCount, lossCount, nTrade, winRate, mdd, profitFactor)
    }
}
