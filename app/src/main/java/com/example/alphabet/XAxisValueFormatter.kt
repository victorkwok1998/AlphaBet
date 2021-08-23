package com.example.alphabet

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class XAxisValueFormatter(private val list: List<String>): IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return list[value.toInt()]
    }
}