package com.example.alphabet

import android.os.Parcelable
import android.util.Log
import com.example.alphabet.api.RetrofitInstance
import com.example.alphabet.util.Constants.Companion.sdfISO
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import retrofit2.HttpException
import yahoofinance.YahooFinance
import yahoofinance.histquotes.HistoricalQuote
import java.util.*

suspend fun getMarketData(
    symbols: List<String>,
    start: Calendar,
    end: Calendar
): Map<String, List<HistoricalQuote>>? = withContext(Dispatchers.IO) {
    try {
        symbols.associateWith { symbol ->
            val r = RetrofitInstance.api.getHistoricalData(
                symbol,
                start.timeInMillis / 1000,
                end.timeInMillis / 1000
            )
            val rawData = csvReader().readAllWithHeader(r.body()!!)
            if (!r.isSuccessful) {
                throw HttpException(r)
            }

            val data = rawData.map { row ->
                val date = row["Date"]?.toCalendar()
                val open = row["Open"]?.toBigDecimalOrNull()
                val high = row["High"]?.toBigDecimalOrNull()
                val low = row["Low"]?.toBigDecimalOrNull()
                val close = row["Close"]?.toBigDecimalOrNull()
                val adjClose = row["Adj Close"]?.toBigDecimalOrNull()
                val volume = row["Volume"]?.toLong()
                if (date != null && open != null && high != null && low != null && close != null && adjClose != null && volume != null) {
                    HistoricalQuote(symbol, date, open, low, high, close, adjClose, volume)
                } else {
                    null
                }
            }
            data.filterNotNull()
        }.toMap()

//        val stocks = YahooFinance.get(symbols.toTypedArray(), true)
//        stocks.map { (k, v) -> k to v.getHistory(start, end, Interval.DAILY) }.toMap()
    } catch (e: Exception){
        Log.e("Market Data", e.toString())
        null
    }
}

suspend fun getStock(symbol: String): StockStatic? {
    return try {
        withContext(Dispatchers.IO) {
            val stock = YahooFinance.get(symbol)
            if (stock.isValid)
                null
//                stock.toStockStatic()
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

fun priceToReturn(dfPrice: DataFrame<*>): DataFrame<*> {
    return dfPrice
        .update { dfsOf<Float>() }.perRowCol { row, col ->
            row[col] / (row.prev()?.get(col) ?: row[col]) - 1
        }
        .drop(1)
}