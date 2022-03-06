package com.example.alphabet

import androidx.compose.runtime.MutableState

data class BacktestInput(
    var symbol: String,
    var strategyInput: StrategyInput
)