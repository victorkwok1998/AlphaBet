package com.example.alphabet.api

import com.example.alphabet.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(JsonOrCsvConverterFactory())
            .build()
    }

    val api: YahooFinanceAPI by lazy {
        retrofit.create(YahooFinanceAPI::class.java)
    }
}