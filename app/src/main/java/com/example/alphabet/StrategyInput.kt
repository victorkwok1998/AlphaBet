package com.example.alphabet

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import kotlinx.serialization.Serializable
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.BaseStrategy
import org.ta4j.core.Rule
import org.ta4j.core.Strategy
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.rules.StopGainRule
import org.ta4j.core.rules.StopLossRule

@Serializable
class StrategyInput(
    var strategyName: String,
    var des: String,
    val entryRulesInput: MutableList<RuleInput>,
    val exitRulesInput: MutableList<RuleInput>,
    var stopGain: String,
    var stopLoss: String,
) {
    fun entryRulesDes(): String {
        return entryRulesInput.joinToString("\n")
    }
    fun exitRulesDes(): String {
        val stopGainText = if(stopGain.isNotEmpty()) "\nStop Gain ($stopGain%)" else ""
        val stopLossText = if(stopLoss.isNotEmpty()) "\nStop Loss ($stopLoss%)" else ""
        return exitRulesInput.joinToString("\n") + stopGainText + stopLossText
    }
    fun toStrategy(series: BaseBarSeries): Strategy {
        val entryRules = entryRulesInput.map { it.parseRule(series) }
        val exitRules = exitRulesInput.map { it.parseRule(series) }

        val nonIndRules = mutableListOf<Rule>()
        if (stopGain.isNotEmpty())
            nonIndRules.add(StopGainRule(ClosePriceIndicator(series), stopGain.toFloat()))
        if (stopLoss.isNotEmpty())
            nonIndRules.add(StopLossRule(ClosePriceIndicator(series), stopLoss.toFloat()))
        val entryRule = aggRule(entryRules, listOf())
        val exitRule = aggRule(exitRules, nonIndRules)
        return BaseStrategy(entryRule, exitRule)
    }
    fun isEmpty(): Boolean {
        return entryRulesInput.isEmpty()
    }
    fun copy(
        strategyName: String = this.strategyName,
        des: String = this.des,
        entryRulesInput: MutableList<RuleInput> = this.entryRulesInput.map { it.copy() }.toMutableList(),
        exitRulesInput: MutableList<RuleInput> = this.exitRulesInput.map { it.copy() }.toMutableList(),
        stopGain: String = this.stopGain,
        stopLoss: String = this.stopLoss
    ) = StrategyInput(strategyName, des, entryRulesInput, exitRulesInput, stopGain, stopLoss)

    fun toCustomStrategyInput(): CustomStrategyInput {
        return CustomStrategyInput(
            mutableStateOf(strategyName),
            mutableStateOf(des),
            entryRulesInput.toMutableStateList(),
            exitRulesInput.toMutableStateList(),
            mutableStateOf(stopGain),
            mutableStateOf(stopLoss),
        )
    }
}