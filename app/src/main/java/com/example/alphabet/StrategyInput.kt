package com.example.alphabet

import kotlinx.serialization.Serializable

@Serializable
data class StrategyInput(
    val des: String,
    val entryRulesInput: MutableList<RuleInput>,
    val exitRulesInput: MutableList<RuleInput>,
    val stopGain: Float?,
    val stopLoss: Float?
)