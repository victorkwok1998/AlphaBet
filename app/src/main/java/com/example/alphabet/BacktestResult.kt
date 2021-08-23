package com.example.alphabet

import kotlinx.serialization.Serializable

@Serializable
data class BacktestResult(
    val name: String,
    val symbol: String,
    val date: List<String>,
    val cashFlow: List<Float>,
)
