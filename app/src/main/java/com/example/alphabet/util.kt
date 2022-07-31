package com.example.alphabet

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.alphabet.MyApplication.Companion.sdfISO
import com.example.alphabet.database.DatabaseViewModel
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.kotlinx.dataframe.math.mean
import org.nield.kotlinstatistics.standardDeviation
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.Rule
import org.ta4j.core.Trade
import org.ta4j.core.TradingRecord
import org.ta4j.core.analysis.CashFlow
import org.ta4j.core.rules.BooleanRule
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlin.math.max
import kotlin.math.sqrt

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

fun String.toCalendar(): Calendar {
    val c = Calendar.getInstance()
    c.time =  sdfISO.parse(this)!!
    return c
}

fun String.toDate(): Date = this.toCalendar().time

fun Calendar.toZonedDateTime(): ZonedDateTime = ZonedDateTime.ofInstant(this.toInstant(), ZoneId.systemDefault())

fun Trade.toTradeData(date: String, adjClose: Float) = TradeData(this.type, this.index, this.pricePerAsset.floatValue(), date, adjClose)

fun ZonedDateTime.toDate() = Date.from(this.toInstant())

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

fun isoToDisplayShort(dateString: String): String {
    return dateString
        .run { MyApplication.sdfISO.parse(this) }
        .run { MyApplication.sdfShort.format(this!!) }
}

fun isoToDisplayLong(dateString: String): String {
    return dateString
        .run { MyApplication.sdfISO.parse(this) }
        .run { MyApplication.sdfLong.format(this!!) }
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

fun sharpeRatio(portRet: List<Float>): Float {
    return (portRet.mean() / portRet.standardDeviation() * sqrt(252.0)).toFloat()
}

fun maxDrawDown(navList: List<Float>): Float {
    var cumMax = navList[0]
    return navList.map { nav ->
        cumMax = max(nav, cumMax)
        nav / cumMax - 1
    }.minOf { it }
}

fun navToReturn(navList: List<Float>): List<Float> {
    return (1 until navList.size).map {
        navList[it] / navList[it-1] - 1
    }
}

fun diff(l: List<Float>): List<Float> {
    return (1 until l.size).map {
        l[it] - l[it - 1]
    }
}

fun setReturnText(context: Context, tv: TextView, ret: Float, formatter: (Float) -> String) {
    tv.text = formatter(ret)
    if (ret > 0)
        tv.setTextColor(context.getColor(R.color.green))
    else
        tv.setTextColor(context.getColor(R.color.red))
}

fun <E> Iterable<E>.updated(index: Int, elem: E) = mapIndexed { i, existing ->  if (i == index) elem else existing }

fun Fragment.hideKeyboard(): Boolean {
    return (context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow((activity?.currentFocus ?: View(context)).windowToken, 0)
}

fun setStrategyChipGroupFilter(chipGroup: ChipGroup, db: DatabaseViewModel, adapter: StrategyListAdapter) {
    chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
        val tmp = when (checkedIds[0]) {
            R.id.all_chip -> db.readAllStrategy.value
            R.id.momentum_chip -> db.filterStrategyByType("Momentum")
            R.id.reversal_chip -> db.filterStrategyByType("Reversal")
            R.id.passive_chip -> db.filterStrategyByType("Passive")
            R.id.custom_chip -> db.filterStrategyByType("Custom")
            else -> listOf()
        }
        tmp?.let { adapter.updateList(it) }
    }
}

fun setTheme(theme: String?) {
    when (theme) {
        "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        "system_default" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}

fun failToDownloadDialog(context: Context) {
    MaterialAlertDialogBuilder(context)
        .setTitle(R.string.error)
        .setMessage(R.string.failed_to_download_data_error)
        .setPositiveButton(R.string.ok, null)
        .show()
}

fun getYesterday(): Calendar = Calendar.getInstance().apply { add(Calendar.DATE, -1) }

fun getBitmapFromView(view: View): Bitmap {
    val bitmap = Bitmap.createBitmap(
        view.width, view.height, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}

fun getBacktestPeriodString(backtestResult: BacktestResult, context: Context): String {
    return context.getString(
        R.string.period_format,
        isoToDisplayLong(backtestResult.date.first()),
        isoToDisplayLong(backtestResult.date.last())
    )
}