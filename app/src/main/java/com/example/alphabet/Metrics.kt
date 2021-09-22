package com.example.alphabet

import kotlinx.serialization.Serializable
import org.ta4j.core.TradingRecord
import org.ta4j.core.reports.TradingStatement

@Serializable
data class Metrics(
    val tradingRecord: TradingRecord,
    val pnl: Double,
    val pnlPct: Double,
    val profitCount: Double,
    val lossCount: Double,
    val nTrade: Double,
    val winRate: Double,
    val mdd: Double,
    val pnlList: List<Double>
)
