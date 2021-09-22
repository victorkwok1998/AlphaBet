package com.example.alphabet

import android.content.Context
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.MyApplication.Companion.sdfISO
import com.example.alphabet.databinding.CreateStrategyBinding
import com.example.alphabet.databinding.FragmentCreateRuleBinding
import com.example.alphabet.databinding.IndicatorParamBinding
import com.example.alphabet.databinding.SelectIndicatorBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.Rule
import org.ta4j.core.TradingRecord
import org.ta4j.core.analysis.CashFlow
import org.ta4j.core.rules.BooleanRule
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*



fun setStratList(context: Context?, createStrategyBinding: CreateStrategyBinding, adapter: StrategyAdapter) {
    createStrategyBinding.stratList.run {
        this.layoutManager = LinearLayoutManager(context)
        this.adapter = adapter
        this.setHasFixedSize(true)
    }
}

fun setParamInput(layout: LinearLayout, indParamList: List<Int>?, paramNameList: List<String>, layoutInflater: LayoutInflater) {
    layout.removeAllViews()
    paramNameList.forEachIndexed { i, v ->
        IndicatorParamBinding.inflate(layoutInflater, layout, false).apply {
            this.paramValueText.hint = v
            // fill param list if not null
            indParamList?.let { this.paramValueText.editText?.setText(it[i].toString()) }
            layout.addView(this.root)
        }
    }
}

fun aggRule(indRules: List<Rule>, nonIndRules: List<Rule>): Rule {
    return when {
        indRules.isNotEmpty() && nonIndRules.isNotEmpty() -> {
            nonIndRules.reduce{ res, rule -> res.or(rule)}.or(indRules.reduce { res, rule -> res.and(rule)})
        }
        indRules.isEmpty() && nonIndRules.isNotEmpty() -> nonIndRules.reduce{ res, rule -> res.or(rule)}
        indRules.isNotEmpty() && nonIndRules.isEmpty() -> indRules.reduce { res, rule -> res.and(rule)}
        else -> BooleanRule(true)
    }
}

fun getCashFlow(series: BaseBarSeries, tradingRecord: TradingRecord): List<Float> {
    val cashFlow = CashFlow(series, tradingRecord)
    return List(cashFlow.size) {i ->  cashFlow.getValue(i).floatValue()}
}

fun createCalandar(year: Int, month: Int, date: Int): Calendar {
    return Calendar.getInstance().also { it.set(year, month, date) }
}

fun stringToCalendar(date: String): Calendar {
    return Calendar.getInstance().apply { time = sdfISO.parse(date)!! }
}

fun getJsonDataFromAsset(context: Context, fileName: String): String {
    val jsonString: String
    try {
        jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        throw ioException
    }
    return jsonString
}

fun readFile(file: File): String {
    return FileInputStream(file).bufferedReader().use { it.readText() }
}

/*
Copy file from asset to internal storage if file does not exist
 */
fun Context.copyFromAsset(fileName: String) {
    val file = File(filesDir, fileName)

    if (!file.exists()) {
        assets.open(fileName).use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}