package com.example.alphabet

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
    val des: String,
    val entryRulesInput: MutableList<RuleInput>,
    val exitRulesInput: MutableList<RuleInput>,
    val stopGain: String,
    val stopLoss: String
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
}