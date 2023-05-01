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
    val transactionCost: CostInput
): Parcelable {
    fun getPnl(): Float {
        val side = if (startingType == Trade.TradeType.BUY) 1 else -1
        val gross = (exit.adjClose - entry.adjClose) * side
        return when(transactionCost.type) {
            CostType.PCT -> {
                gross - entry.adjClose * transactionCost.fee / 100 - exit.adjClose * transactionCost.fee / 100
            }
            CostType.BPS -> {
                gross - entry.adjClose * transactionCost.fee / 10000 - exit.adjClose * transactionCost.fee / 10000
            }
            CostType.FIXED -> {
                gross - transactionCost.fee * 2
            }
        }
    }

    fun getPnlPct(): Float {
        return getPnl() / entry.adjClose
    }
}
