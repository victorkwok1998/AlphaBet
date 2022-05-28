package com.example.alphabet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import yahoofinance.Stock

@Parcelize
@Serializable
data class StockStatic(
    val symbol: String,
    val longname: String?,
    val shortname: String?,
//    val currency: String,
    val exchDisp: String,
    val typeDisp: String
) : Parcelable

//fun Stock.toStockStatic(): StockStatic {
//    return StockStatic(this.symbol, this.name, this.currency)
//}
