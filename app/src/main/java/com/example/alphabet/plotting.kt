package com.example.alphabet

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.TradingRecord
import org.ta4j.core.analysis.CashFlow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

val mainColor = Color.rgb(100, 149, 237)

fun plotEquityCurveFromCashFlow(chart: LineChart, series: BaseBarSeries, tradingRecord: TradingRecord, label: String?, context: Context) {
    val y = getCashFlow(series, tradingRecord)
    val dates = List(series.barCount) {i -> Date.from(series.getBar(i).endTime.toInstant()) }
    plotEquityCurve(chart, dates, y, label, context)
}

fun plotTrades(chart: CombinedChart, series: BaseBarSeries, tradingRecord: TradingRecord, label: String?, context: Context) {
    val y = List(series.barCount) { i -> series.getBar(i).closePrice.floatValue() }
    val dates = List(series.barCount) {i -> Date.from(series.getBar(i).endTime.toInstant()) }
    val buyIndexList = tradingRecord.positions.map { it.entry.index }
    val sellIndexList = tradingRecord.positions.map { it.exit.index }
    plotLineScatterChart(chart,
        dates, y, label,
        listOf(buyIndexList, sellIndexList),
        listOf(R.color.green, R.color.red),
        listOf("Entry", "Exit"),
        context)
}

fun plotEquityCurve(
    chart: LineChart,
    xVals: List<Date>,
    yVals: List<Float>,
    label: String?,
    context: Context,
) {
    val sdf = SimpleDateFormat("MMM yy", Locale.ENGLISH)
    val dates = xVals.map { sdf.format(it) }
    val lineDataSet = List(yVals.size) { i -> Entry(i.toFloat(), yVals[i]) } // convert to Entry
        .run { LineDataSet(this, label) }
        .apply {
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
            setColors(intArrayOf(R.color.blue), context)
        }

    with (chart) {
        data = LineData(lineDataSet)
        setTouchEnabled(false)
        setDrawGridBackground(false)
        setDrawBorders(false)
        setScaleEnabled(false)
        axisLeft.apply {
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
        }
        axisRight.apply{
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }
        xAxis.apply {
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = XAxisValueFormatter(dates)
            granularity = 1f
            isGranularityEnabled = true
            setDrawAxisLine(false)
        }
        description.isEnabled = false
        legend.isEnabled = false
        invalidate()  // plot
    }

}

fun plotMultiLineCurve(chart: LineChart, xVals: List<Date>, yVals: List<List<Float>>, labels: List<String?>, context: Context) {
    val sdf = SimpleDateFormat("MMM yy", Locale.ENGLISH)
    val dates = xVals.map { sdf.format(it) }
    val dataSets = ArrayList<ILineDataSet>()
    yVals.forEachIndexed {i, l ->
        val set = List(l.size) { j -> Entry(j.toFloat(), l[j]) }
            .run{ LineDataSet(this, labels[i]) }
            .apply {
                lineWidth = 3f
                setDrawCircles(false)
                fillAlpha = 255
                setDrawValues(false)
            }
        if (i == yVals.lastIndex) { // TODO
            set.setColors(intArrayOf(R.color.red), context)
        }
        dataSets.add(set)
    }
    with(chart) {
        data = LineData(dataSets)
        setTouchEnabled(false)
        setDrawGridBackground(false)
        setDrawBorders(false)
        axisLeft.apply {
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
        }
        axisRight.setDrawGridLines(false)
        xAxis.apply {
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = XAxisValueFormatter(dates)
            granularity = 1f
            isGranularityEnabled = true
        }
        description.isEnabled = false
        invalidate()
    }
}

fun plotRadarChart(chart: RadarChart, scores: List<Float>, labels: Collection<String>) {
    val radarDataSet = scores.map { RadarEntry(it) }
        .run { RadarDataSet(this, "Strategy Performance") }
        .apply {
            setDrawFilled(true)
            setDrawValues(false)
//            color = mainColor
//            fillColor = mainColor
        }
    with(chart) {
        xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(labels)
            textSize = 14f
        }
        yAxis.apply {
            axisMinimum = 0f
            axisMaximum = 4f
            labelCount = 5
            setDrawLabels(false)
        }
        data = RadarData(radarDataSet)
        legend.isEnabled = false
        description.isEnabled = false
        setTouchEnabled(false)

        invalidate()
    }
}

fun plotStrategyVsBuyAndHold(chart: LineChart, series: BaseBarSeries, tradingRecord: TradingRecord, label: String?, context: Context) {
    val cashFlow = getCashFlow(series, tradingRecord)
    val buyAndHold = List(series.barCount) {i -> series.getBar(i).closePrice.floatValue() / series.getBar(0).closePrice.floatValue() }
    val dates = List(series.barCount) {i -> Date.from(series.getBar(i).endTime.toInstant()) }
    plotMultiLineCurve(chart, dates, listOf(cashFlow, buyAndHold), listOf(label, "Buy And Hold"), context)
}

fun plotLineScatterChart(
    chart: CombinedChart,
    xVals: List<Date>,
    yVals: List<Float>,
    label: String?,
    markerIndices: List<List<Int>>,
    markerColors: List<Int>,
    markerLabels: List<String>,
    context: Context,
) {
    val sdf = SimpleDateFormat("MMM yy", Locale.ENGLISH)
    val dates = xVals.map { sdf.format(it) }

    val lineData = List(yVals.size) { i -> Entry(i.toFloat(), yVals[i]) } // convert to Entry
        .run { LineDataSet(this, label) }
        .apply {
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
            setColors(intArrayOf(R.color.blue), context)
            highLightColor = ContextCompat.getColor(context, R.color.gray)
            highlightLineWidth = 1f
        }
        .run { LineData(this) }

    val scatterDataSets = ArrayList<IScatterDataSet>()
    markerIndices.forEachIndexed { i, indexList ->
        indexList.map { Entry(it.toFloat(), yVals[it]) }
            .run { ScatterDataSet(this, markerLabels[i]) }
            .apply {
                setScatterShape(ScatterChart.ScatterShape.CIRCLE)
                setColors(intArrayOf(markerColors[i]), context)
                scatterShapeSize = 40f
                setDrawValues(false)
                isHighlightEnabled = false

                scatterDataSets.add(this)
            }
    }

    val combinedData = CombinedData()
    combinedData.setData(lineData)
    combinedData.setData(ScatterData(scatterDataSets))

    with (chart) {
        data = combinedData
//        setTouchEnabled(false)
        setDrawGridBackground(false)
        setDrawBorders(false)
        isDoubleTapToZoomEnabled = false
//        setScaleEnabled(false)
        axisLeft.apply {
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
        }
        axisRight.apply{
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }
        xAxis.apply {
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = XAxisValueFormatter(dates)
            granularity = 1f
            isGranularityEnabled = true
            setDrawAxisLine(false)
        }
        description.isEnabled = false
        legend.isEnabled = false
        invalidate()  // plot
    }
}
