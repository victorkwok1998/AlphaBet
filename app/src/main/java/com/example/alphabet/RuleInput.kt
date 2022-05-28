package com.example.alphabet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.Rule
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.indicators.helpers.ConstantIndicator
import org.ta4j.core.rules.*

@Parcelize
@Serializable
class RuleInput(
    val indInput1: IndicatorInput,
    val indInput2: IndicatorInput,
    var condName: Cond
) : Parcelable {
    fun parseRule(series: BaseBarSeries): Rule {
        val close = ClosePriceIndicator(series)
        if (indInput1.indType == IndType.OTHER) {
            return when (indInput1.indName) {
                "Stop Gain" -> StopGainRule(close, indInput1.indParamList[0].toFloat())
                "Stop Loss" -> StopLossRule(close, indInput1.indParamList[0].toFloat())
                "Trailing Stop Loss" -> TrailingStopLossRule(close, series.numOf(indInput1.indParamList[0].toFloat()))
                "Buy at Start" -> FixedRule(0)
                "Sell at End" -> FixedRule(series.endIndex)
                else -> throw IllegalArgumentException("Cannot find ${indInput1.indName} rule")
            }
        }

        val ind1 = when (indInput1.indType) {
            IndType.INDICATOR -> indInput1.calIndicator(series)
            else -> close
        }
        val ind2 = when (indInput2.indType) {
            IndType.INDICATOR -> indInput2.calIndicator(series)
            IndType.VALUE -> ConstantIndicator(
                series,
                ind1.numOf(indInput2.indParamList[0].toInt())
            )  // convert to Indicator<Num>
            else -> throw IllegalArgumentException("Indicator 2 cannot be a bool")
        }
        return when (condName) {
            Cond.CROSS_UP -> CrossedUpIndicatorRule(ind1, ind2)
            Cond.CROSS_DOWN -> CrossedDownIndicatorRule(ind1, ind2)
            Cond.OVER -> OverIndicatorRule(ind1, ind2)
            Cond.UNDER -> UnderIndicatorRule(ind1, ind2)
        }
    }

    fun isValid(): Boolean {
        if (indInput1.indName.isEmpty()) {
            return false
        }
        return true
    }

    override fun toString(): String {
        if (indInput1.indType == IndType.OTHER)
            return indInput1.toString()
        val condNameString = when (condName) {
            Cond.CROSS_UP -> "cross up"
            Cond.CROSS_DOWN -> "cross down"
            Cond.OVER -> "is over"
            Cond.UNDER -> "is under"
        }
        return "$indInput1 $condNameString $indInput2"
    }

    fun copy(
        indInput1: IndicatorInput = this.indInput1.copy(),
        indInput2: IndicatorInput = this.indInput2.copy(),
        condName: Cond = this.condName
    ) = RuleInput(indInput1, indInput2, condName)

    companion object {
        fun getEmptyRule() = RuleInput(
            IndicatorInput.getEmptyIndicator(),
            IndicatorInput.getEmptyIndicator(),
            Cond.CROSS_UP
        )
    }
}