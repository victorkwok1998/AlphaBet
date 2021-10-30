package com.example.alphabet

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList

class CustomStrategyInput(
    val strategyName: MutableState<String>,
    val des: MutableState<String>,
    val entryRulesInput: MutableList<RuleInput>,
    val exitRulesInput: MutableList<RuleInput>,
    val stopGain: MutableState<String>,
    val stopLoss: MutableState<String>,
) {
    fun toStrategyInput(): StrategyInput {
        return StrategyInput(
            strategyName.value,
            des.value,
            entryRulesInput.toMutableList(),
            exitRulesInput.toMutableList(),
            stopGain.value,
            stopLoss.value
        )
    }
}
