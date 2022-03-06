package com.example.alphabet

import kotlinx.serialization.Serializable
import yahoofinance.Stock

@Serializable
data class StockStatic(
    val symbol: String,
    val name: String
)

fun Stock.toStockStatic(): StockStatic {
    return StockStatic(this.symbol, this.name)
}
