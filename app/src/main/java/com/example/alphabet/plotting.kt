package com.example.alphabet

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.TradingRecord
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

val plotColors = listOf(
    R.color.plotColor1,
    R.color.plotColor2,
    R.color.plotColor3,
    R.color.plotColor4,
    R.color.plotColor5
)

fun plotEquityCurveFromCashFlow(
    chart: LineChart,
    seriesTradingRecord: List<Pair<BaseBarSeries, TradingRecord>>,
    labels: List<String?>,
    enabledLines: List<Boolean>,
    context: Context
) {
    val yVals = seriesTradingRecord.map { (series, tradingRecord) ->
        getCashFlow(series, tradingRecord)
    }
    val dates = List(seriesTradingRecord[0].first.barCount) {i -> Date.from(seriesTradingRecord[0].first.getBar(i).endTime.toInstant()) }
    plotMultiLineCurve(chart, dates, yVals, labels, enabledLines, context)
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
            color = context.getColor(R.color.blue)
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

fun plotMultiLineCurve(
    chart: LineChart,
    xVals: List<Date>,
    yVals: List<List<Float>>,
    labels: List<String?>,
    enabledLines: List<Boolean>,
    context: Context
) {
    val sdf = SimpleDateFormat("MMM yy", Locale.ENGLISH)
    val dates = xVals.map { sdf.format(it) }
    val dataSets = ArrayList<ILineDataSet>()
    yVals.zip(enabledLines).forEachIndexed { i, (l, isEnabled) ->
        if (isEnabled) {
            val set = List(l.size) { j -> Entry(j.toFloat(), l[j]) }
                .run{ LineDataSet(this, labels[i]) }
                .apply {
                    lineWidth = 2f
                    setDrawCircles(false)
                    fillAlpha = 255
                    setDrawValues(false)
                    color = context.getColor(plotColors[i % plotColors.size])
                }
            dataSets.add(set)
        }
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
        axisRight.apply {
            setDrawGridLines(false)
            textSize = 13f
            textColor = context.getColorThemeRes(android.R.attr.textColorPrimary)
        }
        xAxis.apply {
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = XAxisValueFormatter(dates)
            granularity = 1f
            isGranularityEnabled = true
            textSize = 13f
            extraBottomOffset = 5f
            spaceMin = 10f
            labelCount = 5
            textColor = context.getColorThemeRes(android.R.attr.textColorPrimary)
        }
        description.isEnabled = false
//        legend.apply {
//            textSize = 15f
//            xEntrySpace = 30f
//        }
        legend.isEnabled = false
        invalidate()
    }
}

fun plotRadarChart(
    chart: RadarChart,
    scoresList: List<List<Float>>,
    labels: Collection<String>,
    enabledLines: List<Boolean>,
    context: Context
) {
    val radarDataSets = RadarData()
    scoresList.forEachIndexed { i, scores ->
        if (enabledLines[i]) {
            val set = scores.map { RadarEntry(it) }
                .run { RadarDataSet(this, "Strategy Performance") }
                .apply {
                    setDrawFilled(true)
                    setDrawValues(false)
                    fillDrawable =
                        ContextCompat.getDrawable(context, plotColors[i % plotColors.size]).apply {
                            this?.alpha = 50
                        }
                    setColors(intArrayOf(plotColors[i % plotColors.size]), context)
//            fillColor = mainColor
                }
            radarDataSets.addDataSet(set)
        }
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
        data = radarDataSets
        legend.isEnabled = false
        description.isEnabled = false
        setTouchEnabled(false)

        invalidate()
    }
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
            isHighlightEnabled = false
//            highLightColor = ContextCompat.getColor(context, R.color.gray)
//            highlightLineWidth = 1f
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
//            spaceMin = 10f
//            spaceMax = 10f
//            axisMaximum = xVals.lastIndex.toFloat()
//            axisMinimum = 0f
        }
        description.isEnabled = false
        legend.isEnabled = false
        invalidate()  // plot
    }
}

fun plotPieChart(pieChart: PieChart, labelToVal: Map<String, Float>, label: String, context: Context) {
    val pieEntryList = labelToVal.map { PieEntry(it.value, it.key) }
    val pieDataSet = PieDataSet(pieEntryList, label)
        .apply {
            valueTextSize = 12f
            colors = plotColors.map { context.getColor(it) }
        }

    val pieData = PieData(pieDataSet)
    with(pieChart) {
        data = pieData
        description = null
        setHoleColor(Color.TRANSPARENT)
        legend.textColor = context.getColorThemeRes(android.R.attr.textColorPrimary)
        isRotationEnabled = false
        invalidate()
    }
}

@ColorInt
fun Context.getColorThemeRes(@AttrRes id: Int): Int {
    val resolvedAttr = TypedValue()
    this.theme.resolveAttribute(id, resolvedAttr, true)
    return this.getColor(resolvedAttr.resourceId)
}
