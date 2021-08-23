package com.example.alphabet

import org.ta4j.core.BaseBarSeries
import org.ta4j.core.Indicator
import org.ta4j.core.Rule
import org.ta4j.core.indicators.*
import org.ta4j.core.indicators.bollinger.*
import org.ta4j.core.indicators.statistics.*
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.indicators.helpers.ConstantIndicator
import org.ta4j.core.num.Num
import org.ta4j.core.rules.CrossedDownIndicatorRule
import org.ta4j.core.rules.CrossedUpIndicatorRule
import kotlinx.serialization.*
import org.ta4j.core.rules.OverIndicatorRule
import org.ta4j.core.rules.UnderIndicatorRule

@Serializable
class RuleInput(
    val indInput1: IndicatorInput,
    val indInput2: IndicatorInput,
    val condName: Cond
) {
    fun parseRule(series: BaseBarSeries): Rule {
        val close = ClosePriceIndicator(series)
        val ind1 = when (indInput1.indType) {
            IndType.INDICATOR -> calIndicator(indInput1.indName, indInput1.indParamList, series)
            else -> close
        }
        val ind2 = when (indInput2.indType) {
            IndType.INDICATOR -> calIndicator(indInput2.indName, indInput2.indParamList, series)
            IndType.VALUE -> ConstantIndicator(
                series,
                ind1.numOf(indInput2.indName.toInt())
            )  // convert to Indicator<Num>
            IndType.PRICE -> close
        }
        return when (condName) {
            Cond.CROSS_UP -> CrossedUpIndicatorRule(ind1, ind2)
            Cond.CROSS_DOWN -> CrossedDownIndicatorRule(ind1, ind2)
            Cond.OVER -> OverIndicatorRule(ind1, ind2)
            Cond.UNDER -> UnderIndicatorRule(ind1, ind2)
        }
    }

    private fun calIndicator (name: String, params: List<Int>, series: BaseBarSeries): Indicator<Num> {
        val close = ClosePriceIndicator(series)
        return when (name) {
            "RSI" -> RSIIndicator(close, params[0])
            "SMA" -> SMAIndicator(close, params[0])
            "EMA" -> EMAIndicator(close, params[0])
            "MACD Histogram" -> MACDHistogramIndicator(close, params[0], params[1], params[2])
            "Bollinger Band Upper" -> BollingerBandsMiddleIndicator(SMAIndicator(close, params[0])).run { BollingerBandsUpperIndicator(this, StandardDeviationIndicator(close, params[0]), numOf(params[1])) }
            "Bollinger Band Lower" -> BollingerBandsMiddleIndicator(SMAIndicator(close, params[0])).run { BollingerBandsLowerIndicator(this, StandardDeviationIndicator(close, params[0]), numOf(params[1])) }
            "Stochastic Oscillator K" -> StochasticOscillatorKIndicator(series, params[0])
            else -> close
        }
    }

    override fun toString(): String {
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