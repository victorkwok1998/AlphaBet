package com.example.alphabet

import androidx.lifecycle.ViewModel

class StaticDataViewModel: ViewModel() {
    var indicatorStatic = listOf<IndicatorStatic>()
    var indToParamList = mapOf<String, List<String>>()

//    var indToParamList: MutableLiveData<Map<String, List<String>>> = MutableLiveData()
//    var stratToDes: MutableLiveData<Map<String, String>> = MutableLiveData()
//    val myBacktestResults = mutableStateListOf<BacktestResult>()
    var radarChartRange = mapOf<String, List<Float>>()
}