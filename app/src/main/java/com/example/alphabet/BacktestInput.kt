package com.example.alphabet

import androidx.compose.runtime.MutableState

data class BacktestInput(
    val symbol: MutableState<String>,
    var strategyInput: StrategyInput
)