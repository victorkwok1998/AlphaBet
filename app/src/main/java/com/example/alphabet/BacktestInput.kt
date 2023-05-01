package com.example.alphabet

import android.os.Parcelable
import com.example.alphabet.database.StrategySchema
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
class BacktestInput(
    var stock: StockStatic,
    var strategyInput: StrategySchema,
    val transactionCost: CostInput
): Parcelable {
    fun getShortName() = "${stock.symbol}, ${strategyInput.strategy.strategyName}"
}