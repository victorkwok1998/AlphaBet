package com.example.alphabet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
class BacktestInput(
    var stock: StockStatic,
    var strategyInput: StrategyInput,
): Parcelable {
    fun getShortName() = "${stock.symbol}, ${strategyInput.strategyName}"
}