package com.example.alphabet

data class Metrics(
    val pnl: Double,
    val pnlPct: Double,
    val profitCount: Double,
    val lossCount: Double,
    val nTrade: Double,
    val winRate: Double,
    val mdd: Double,
    val pnlList: List<Double>
)
