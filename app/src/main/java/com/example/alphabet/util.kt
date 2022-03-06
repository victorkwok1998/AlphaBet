package com.example.alphabet

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.example.alphabet.MyApplication.Companion.pct
import com.example.alphabet.MyApplication.Companion.sdfISO
import com.example.alphabet.MyApplication.Companion.sdfLong
import com.example.alphabet.databinding.DialogTimePeriodBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.dataframe.math.mean
import org.nield.kotlinstatistics.standardDeviation
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.Rule
import org.ta4j.core.TradingRecord
import org.ta4j.core.analysis.CashFlow
import org.ta4j.core.rules.BooleanRule
import yahoofinance.YahooFinance
import yahoofinance.histquotes.HistoricalQuote
import yahoofinance.histquotes.Interval
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.Serializable
import java.util.*
import kotlin.math.sqrt


fun parameterDialog(
    indicatorInput: IndicatorInput,
    primSec: Serializable?,
    childFragmentManager: FragmentManager,
    isPopBackStack: Boolean = true,
) {
    ParameterDialogFragment.newInstance(indicatorInput, primSec, isPopBackStack)
        .show(
            childFragmentManager,
            ParameterDialogFragment.TAG
        )
}

//fun setParamInput(layout: LinearLayout, indParamList: List<Int>?, paramNameList: List<String>, layoutInflater: LayoutInflater) {
//    layout.removeAllViews()
//    paramNameList.forEachIndexed { i, v ->
//        IndicatorParamBinding.inflate(layoutInflater, layout, false).apply {
//            this.paramValueText.hint = v
//            // fill param list if not null
//            indParamList?.let { this.paramValueText.editText?.setText(it[i].toString()) }
//            layout.addView(this.root)
//        }
//    }
//}

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

fun createCalendar(year: Int, month: Int, date: Int): Calendar {
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

fun isoToDisplay(dateString: String): String {
    return dateString
        .run { MyApplication.sdfISO.parse(this) }
        .run { MyApplication.sdfShort.format(this!!) }
}

fun datePickerDialog(context: Context, defaultDate: Calendar, onDateSet: (Calendar) -> Unit) {
    val year = defaultDate.get(Calendar.YEAR)
    val month = defaultDate.get(Calendar.MONTH)
    val day = defaultDate.get(Calendar.DAY_OF_MONTH)

    val dpd = DatePickerDialog(
        context,
        R.style.MySpinnerDatePickerStyle,
        { view, mYear, mMonth, mDay ->
            onDateSet(createCalendar(mYear, mMonth, mDay))
        },
        year,
        month,
        day
    )
    dpd.show()
}

fun switchVisibility(v: View) {
    if (v.visibility == View.VISIBLE)
        v.visibility = View.GONE
    else
        v.visibility = View.VISIBLE
}

fun sharpeRatio(portRet: List<Float>): Float {
    return (portRet.mean() / portRet.standardDeviation() * sqrt(252.0)).toFloat()
}

fun setReturnText(context: Context, tv: TextView, ret: Float, formatter: (Float) -> String) {
    tv.text = formatter(ret)
    if (ret > 0)
        tv.setTextColor(context.getColor(R.color.green))
    else
        tv.setTextColor(context.getColor(R.color.red))
}