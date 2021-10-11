package com.example.alphabet

import kotlinx.serialization.Serializable

@Serializable
data class BacktestResult(
//    val strategyName: String,
    val symbol: String,
    val strategyInput: StrategyInput,
    val date: List<String>,
    val cashFlow: List<Float>,
)
