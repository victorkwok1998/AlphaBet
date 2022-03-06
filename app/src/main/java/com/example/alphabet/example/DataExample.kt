package com.example.alphabet.example

import com.example.alphabet.*

class DataExample {
    val entry = RuleInput(
        IndicatorInput(IndType.INDICATOR, "RSI", mutableListOf("14")), IndicatorInput(
            IndType.VALUE, "30", mutableListOf()), Cond.CROSS_DOWN)
    val exit = RuleInput(
        IndicatorInput(IndType.INDICATOR, "RSI", mutableListOf("14")), IndicatorInput(
            IndType.VALUE, "70", mutableListOf()), Cond.CROSS_UP)
    val other = RuleInput(
        indInput1 = IndicatorInput(IndType.OTHER, "Stop Gain", mutableListOf()),
        indInput2 = IndicatorInput(IndType.INDICATOR, "", mutableListOf()),
        condName = Cond.CROSS_UP
    )
    val strategyInput = StrategyInput(
        "RSI Strategy",
        "Trade based on current and historical strength and weakness of a stock",
        mutableListOf(entry),
        mutableListOf(exit)
    )

}