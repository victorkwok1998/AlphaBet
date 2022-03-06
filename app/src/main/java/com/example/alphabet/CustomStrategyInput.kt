package com.example.alphabet

import androidx.compose.runtime.MutableState

class CustomStrategyInput(
    val strategyName: MutableState<String>,
    val des: MutableState<String>,
    val entryRulesInput: MutableList<RuleInput>,
    val exitRulesInput: MutableList<RuleInput>,
) {
    fun toStrategyInput(): StrategyInput {
        return StrategyInput(
            strategyName.value,
            des.value,
            entryRulesInput.toMutableList(),
            exitRulesInput.toMutableList(),
            strategyType = "Custom"
        )
    }
}
