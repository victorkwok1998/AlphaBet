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
    fun getPnl(): Float {
        val side = if (startingType == Trade.TradeType.BUY) 1 else -1
        return (exit.adjClose - entry.adjClose) * side
    }

    fun getPnlPct(): Float {
        return getPnl() / entry.adjClose
    }
}
