package com.example.alphabet

import org.ta4j.core.BaseBarSeries
import org.ta4j.core.Rule
import kotlinx.serialization.*
import org.ta4j.core.indicators.helpers.*
import org.ta4j.core.rules.*

@Serializable
class RuleInput(
    val indInput1: IndicatorInput,
    val indInput2: IndicatorInput,
    var condName: Cond
) {
    fun parseRule(series: BaseBarSeries): Rule {
        if (indInput1.indType == IndType.BOOL) {
            return if (indInput1.indName == "Buy at start") FixedRule(0)  else FixedRule(series.endIndex)
        }
        val close = ClosePriceIndicator(series)
        val ind1 = when (indInput1.indType) {
            IndType.INDICATOR -> indInput1.calIndicator(series)
            else -> close
        }
        val ind2 = when (indInput2.indType) {
            IndType.INDICATOR -> indInput2.calIndicator(series)
            IndType.VALUE -> ConstantIndicator(
                series,
                ind1.numOf(indInput2.indName.toInt())
            )  // convert to Indicator<Num>
            IndType.PRICE -> close
            else -> throw IllegalArgumentException("Indicator 2 cannot be a bool")
        }
        return when (condName) {
            Cond.CROSS_UP -> CrossedUpIndicatorRule(ind1, ind2)
            Cond.CROSS_DOWN -> CrossedDownIndicatorRule(ind1, ind2)
            Cond.OVER -> OverIndicatorRule(ind1, ind2)
            Cond.UNDER -> UnderIndicatorRule(ind1, ind2)
        }
    }

    override fun toString(): String {
        if (indInput1.indType == IndType.BOOL)
            return indInput1.indName
        val paramString1 = if (indInput1.indParamList.isNotEmpty()) indInput1.indParamList.joinToString(prefix = "(", postfix = ")") else ""
        val paramString2 = if (indInput2.indParamList.isNotEmpty()) indInput2.indParamList.joinToString(prefix = "(", postfix = ")") else ""
        val condNameString = when (condName) {
            Cond.CROSS_UP -> "cross up"
            Cond.CROSS_DOWN -> "cross down"
            Cond.OVER -> "is over"
            Cond.UNDER -> "is under"
        }
        return "${indInput1.indName}${paramString1} $condNameString ${indInput2.indName}${paramString2}"
    }
}