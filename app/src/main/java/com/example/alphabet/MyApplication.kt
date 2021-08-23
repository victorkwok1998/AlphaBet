package com.example.alphabet

import android.app.Application
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class MyApplication: Application() {
    companion object {
        // global variable
        var sdfISO = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        var sdfShort = SimpleDateFormat("MMM yy", Locale.ENGLISH)
        var sdfLong = SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
        val dec = DecimalFormat("#,###0.00")
        val intFormat = DecimalFormat("#,##0")
        val pct = DecimalFormat("#,###0.00%")
    }
}