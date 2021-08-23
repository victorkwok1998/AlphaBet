package com.example.alphabet

import kotlinx.serialization.Serializable

@Serializable
data class BacktestInput(
    val symbol: String,
    val startDate: String,
    val endDate: String,
    val strategyName: StrategyName
)