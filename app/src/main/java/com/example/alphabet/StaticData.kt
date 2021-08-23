package com.example.alphabet

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
    var indToParamList: MutableLiveData<Map<String, List<String>>> = MutableLiveData()
//    var stratToDes: MutableLiveData<Map<String, String>> = MutableLiveData()
    var defaultStrategy: MutableLiveData<Map<StrategyName, StrategyInput>> = MutableLiveData()
    var myStrategy: MutableLiveData<BacktestResult> = MutableLiveData()
    var radarChartRange: MutableLiveData<Map<String, List<Float>>> = MutableLiveData()
}