package com.example.alphabet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.ta4j.core.TradingRecord
import org.ta4j.core.reports.TradingStatement

@Parcelize
@Serializable
data class Metrics(
    val pnl: Float,
    val pnlPct: Float,
    val profitCount: Int,
    val lossCount: Int,
    val nTrade: Int,
    val winRate: Float,
    val mdd: Float,
    val profitFactor: Float
): Parcelable
