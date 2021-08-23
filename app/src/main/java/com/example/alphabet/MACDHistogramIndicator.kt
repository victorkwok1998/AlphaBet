package com.example.alphabet

import org.ta4j.core.Indicator
import org.ta4j.core.indicators.CachedIndicator
import org.ta4j.core.indicators.EMAIndicator
import org.ta4j.core.indicators.MACDIndicator
import org.ta4j.core.num.Num

class MACDHistogramIndicator(
    indicator: Indicator<Num>,
    shortBarCount: Int,
    longBarCount: Int,
    emaBarCount: Int
) : CachedIndicator<Num>(indicator) {
    private val macdLine = MACDIndicator(indicator, shortBarCount, longBarCount)
    private val singleLine = EMAIndicator(macdLine, emaBarCount)

    override fun calculate(index: Int): Num {
        return macdLine.getValue(index).minus(singleLine.getValue(index))
    }
}