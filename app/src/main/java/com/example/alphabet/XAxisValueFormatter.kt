package com.example.alphabet

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class XAxisValueFormatter(private val list: List<String>): IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        if (index < 0 || index >= list.size)
            return ""
        return list[value.toInt()]
    }
}