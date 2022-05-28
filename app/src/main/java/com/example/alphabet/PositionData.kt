package com.example.alphabet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.ta4j.core.Trade

@Parcelize
@Serializable
data class PositionData(
    val entry: TradeData,
    val exit: TradeData,
    val startingType: Trade.TradeType,
): Parcelable {
    fun getPnl(adjPriceList: List<Float>): Float {
        val side = if (startingType == Trade.TradeType.BUY) 1 else -1
        return (adjPriceList[exit.index] - adjPriceList[entry.index]) * side
    }

    fun getPnlPct(adjPriceList: List<Float>): Float {
        return (getPnl(adjPriceList) / adjPriceList[entry.index] - 1) * 100
    }
}
