package com.example.alphabet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.BaseStrategy
import org.ta4j.core.Strategy

@Parcelize
@Serializable
class StrategyInput(
    var strategyName: String,
    var des: String,
    var entryRulesInput: MutableList<RuleInput>,
    var exitRulesInput: MutableList<RuleInput>,
    val strategyType: String? = null
) : Parcelable {
    fun entryRulesDes(): String {
        return entryRulesInput.joinToString("\n")
    }
    fun exitRulesDes(): String {
//        val stopGainText = if(stopGain.isNotEmpty()) "\nStop Gain ($stopGain%)" else ""
//        val stopLossText = if(stopLoss.isNotEmpty()) "\nStop Loss ($stopLoss%)" else ""
        return exitRulesInput.joinToString("\n")
    }
    fun toStrategy(series: BaseBarSeries): Strategy {
        val entryRules = entryRulesInput.filter { it.indInput1.indType != IndType.OTHER }
            .map { it.parseRule(series) }
        val exitRules = exitRulesInput.filter { it.indInput1.indType != IndType.OTHER }
            .map { it.parseRule(series) }
        val entryNonIndRules = entryRulesInput.filter { it.indInput1.indType == IndType.OTHER }
            .map { it.parseRule(series) }
        val exitNonIndRules = exitRulesInput.filter { it.indInput1.indType == IndType.OTHER }
            .map { it.parseRule(series) }

//        val nonIndRules = mutableListOf<Rule>()
//        if (stopGain.isNotEmpty())
//            nonIndRules.add(StopGainRule(ClosePriceIndicator(series), stopGain.toFloat()))
//        if (stopLoss.isNotEmpty())
//            nonIndRules.add(StopLossRule(ClosePriceIndicator(series), stopLoss.toFloat()))
        val entryRule = aggRule(entryRules, entryNonIndRules)
        val exitRule = aggRule(exitRules, exitNonIndRules)
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
    ) = StrategyInput(strategyName, des, entryRulesInput, exitRulesInput, strategyType)


    fun indicatorUsed(): List<String> {
        val res = entryRulesInput.map { it.indInput1.indName } union
                entryRulesInput.map { it.indInput2.indName } union
                exitRulesInput.map { it.indInput1.indName } union
                exitRulesInput.map { it.indInput2.indName }
        return res.filter { it.isNotEmpty() }
    }

    companion object {
        fun getEmptyStrategy(): StrategyInput {
            return StrategyInput(
                "Custom Strategy",
                "Custom Strategy",
                mutableListOf(),
                mutableListOf(),
                "Custom"
            )
        }
    }
}