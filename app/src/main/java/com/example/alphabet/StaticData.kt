package com.example.alphabet

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.ta4j.core.indicators.*
import org.ta4j.core.Indicator
import org.ta4j.core.indicators.bollinger.*
import org.ta4j.core.num.Num
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KFunction

class StaticDataViewModel: ViewModel() {
    val indToParamList = mutableStateOf(mapOf<String, List<String>>())
//    var indToParamList: MutableLiveData<Map<String, List<String>>> = MutableLiveData()
//    var stratToDes: MutableLiveData<Map<String, String>> = MutableLiveData()
    val defaultStrategy = mutableStateOf(listOf<StrategyInput>())
    val myBacktestResults = mutableStateListOf<BacktestResult>()
    var radarChartRange = mutableStateOf(mapOf<String, List<Float>>())
}