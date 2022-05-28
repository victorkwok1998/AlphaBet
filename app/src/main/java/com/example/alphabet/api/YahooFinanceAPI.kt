package com.example.alphabet.api

import com.example.alphabet.StockStatic
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.reflect.Type

interface YahooFinanceAPI {
    @GET("v7/finance/download/{symbol}") @Csv
    suspend fun getHistoricalData(
    @Path("symbol") symbol: String,
    @Query("period1") start: Long,
    @Query("period2") end: Long,
    @Query("interval") interval: String = "1d",
    @Query("events") events: String = "history"
): Response<String>

    @GET("v1/finance/search")
    suspend fun searchSymbol(
        @Query("q") symbol: String,
        @Query("newsCount") newCount: Int = 0
    ): Response<SymbolSearchResult>
}

data class SymbolSearchResult(
    val quotes: List<StockStatic>
)

//@Retention(AnnotationRetention.RUNTIME)
//annotation class Json

@Retention(AnnotationRetention.RUNTIME)
annotation class Csv

class JsonOrCsvConverterFactory: Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        for (a in annotations) {
            if (a.annotationClass == Csv::class) {
                return ScalarsConverterFactory.create().responseBodyConverter(type, annotations, retrofit)
            }
        }
        return GsonConverterFactory.create().responseBodyConverter(type, annotations, retrofit)
    }
}