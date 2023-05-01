package com.example.alphabet.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class Constants {
    companion object {
        const val BASE_URL = "https://query1.finance.yahoo.com"
        val sdfISO = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        var sdfLong = SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH)
        var sdfShort = SimpleDateFormat("MMM yy", Locale.ENGLISH)
        val sdfDDMM = SimpleDateFormat("dd/MM/yy", Locale.ENGLISH)
        val pct = DecimalFormat("#,###0.00%")
        val twoDp = DecimalFormat("#,###.##")
    }
}