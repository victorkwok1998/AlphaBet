package com.example.alphabet

import kotlinx.serialization.Serializable
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.Rule
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.rules.FixedRule
import org.ta4j.core.rules.StopGainRule
import org.ta4j.core.rules.StopLossRule
import org.ta4j.core.rules.TrailingStopLossRule

@Serializable
class NonIndicatorRuleInput(
    val ruleName: String,
    val paramList: MutableList<String>
) {
    fun parseRule(series: BaseBarSeries): Rule {
        val close = ClosePriceIndicator(series)
        return when (ruleName) {
            "Stop Gain" -> StopGainRule(close, paramList[0].toFloat())
            "Stop Loss" -> StopLossRule(close, paramList[0].toFloat())
            "Trailing Stop Loss" -> TrailingStopLossRule(close, series.numOf(paramList[0].toFloat()))
            "Buy at Start" -> FixedRule(0)
            "Sell at End" -> FixedRule(series.endIndex)
            else -> throw IllegalArgumentException("Cannot find $ruleName rule")
        }
    }

    override fun toString(): String {
        return if (paramList.isNotEmpty()) paramList.joinToString(prefix = "(", postfix = ")") else ""
    }
}