package com.example.alphabet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.Indicator
import org.ta4j.core.indicators.*
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator
import org.ta4j.core.indicators.helpers.*
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator
import org.ta4j.core.num.Num

@Parcelize
@Serializable
class IndicatorInput(
    var indType: IndType,
    var indName: String,
    var indParamList: MutableList<String>
): Parcelable {
    fun calIndicator (series: BaseBarSeries): Indicator<Num> {
        val paramsInt = indParamList.map { it.toInt() }
        val close = ClosePriceIndicator(series)
        return when (indName) {
            "RSI" -> RSIIndicator(close, paramsInt[0])
            "SMA" -> SMAIndicator(close, paramsInt[0])
            "EMA" -> EMAIndicator(close, paramsInt[0])
            "MACD Histogram" -> MACDHistogramIndicator(close, paramsInt[0], paramsInt[1], paramsInt[2])
            "MACD Line" -> MACDIndicator(close, paramsInt[0], paramsInt[1])
            "Bollinger Band Upper" -> BollingerBandsMiddleIndicator(SMAIndicator(close, paramsInt[0])).run { BollingerBandsUpperIndicator(this, StandardDeviationIndicator(close, paramsInt[0]), numOf(paramsInt[1])) }
            "Bollinger Band Lower" -> BollingerBandsMiddleIndicator(SMAIndicator(close, paramsInt[0])).run { BollingerBandsLowerIndicator(this, StandardDeviationIndicator(close, paramsInt[0]), numOf(paramsInt[1])) }
            "Stochastic Oscillator K" -> StochasticOscillatorKIndicator(series, paramsInt[0])
            "ATR" -> ATRIndicator(series, paramsInt[0])
            "Awesome Oscillator" -> AwesomeOscillatorIndicator(MedianPriceIndicator(series), paramsInt[0], paramsInt[1])
            "Open Price" -> OpenPriceIndicator(series)
            "High Price" -> HighPriceIndicator(series)
            "Low Price" -> LowPriceIndicator(series)
            "Close Price" -> close
            "Double EMA" -> DoubleEMAIndicator(close, paramsInt[0])
            "Triple EMA" -> TripleEMAIndicator(close, paramsInt[0])
            "HMA" -> HMAIndicator(close, paramsInt[0])
            "LWMA" -> LWMAIndicator(close, paramsInt[0])
            "MMA" -> MMAIndicator(close, paramsInt[0])
            "WMA" -> WMAIndicator(close, paramsInt[0])
            "Constant" -> ConstantIndicator(series, close.numOf(paramsInt[0]))
            else -> throw IllegalArgumentException("Cannot find $indName Indicator")
        }
    }
    fun copy(
        indType: IndType = this.indType,
        indName: String = this.indName,
        indParamList: MutableList<String> = this.indParamList.toMutableList()
    ) = IndicatorInput(indType, indName, indParamList)

    companion object{
        val EMPTY_INDICATOR = IndicatorInput(IndType.INDICATOR, "", mutableListOf())
    }
}
