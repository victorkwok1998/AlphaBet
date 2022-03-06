package com.example.alphabet

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.join
import org.jetbrains.kotlinx.dataframe.api.toColumn
import yahoofinance.Stock
import yahoofinance.YahooFinance
import yahoofinance.histquotes.HistoricalQuote
import yahoofinance.histquotes.Interval
import java.util.*

suspend fun getMarketData(
    symbols: List<String>,
    start: Calendar,
    end: Calendar
): Map<String, List<HistoricalQuote>>? = withContext(
    Dispatchers.IO) {
    try {
        val stocks = YahooFinance.get(symbols.toTypedArray(), true)
        stocks.map { (k, v) -> k to v.getHistory(start, end, Interval.DAILY) }.toMap()
    } catch (e: Exception){
        null
    }
}

suspend fun getStock(symbol: String): StockStatic? {
    return try {
        withContext(Dispatchers.IO) {
            val stock = YahooFinance.get(symbol)
            if (stock.isValid)
                stock.toStockStatic()
            else
                null
        }
    } catch (e: Exception) {
        null
    }
}

suspend fun isValid(symbol: String): Boolean {
    return try {
        withContext(Dispatchers.IO) {
            YahooFinance.get(symbol).isValid
        }
    } catch(e: Exception) {
        false
    }
}

suspend fun getClosePrice(symbols: List<String>, start: Calendar, end: Calendar): DataFrame<*>? {
    val stocks = getMarketData(symbols, start, end)

    return if (stocks != null) {
        val dfs = stocks.map { (symbol, stock) ->
            val nonNullQuote = stock.filter { it.adjClose != null }
            val close = nonNullQuote.map { it.adjClose.toFloat() }
            val dateList = nonNullQuote.map { it.date }
            dataFrameOf(
                close.toColumn(symbol),
                dateList.toColumn("date")
            )
        }

        dfs.reduce { acc, df -> acc.join(df) {"date" match "date"} }
    } else
        null
}