package com.example.alphabet

import kotlinx.serialization.Serializable

@Serializable
data class IndicatorInput(
    val indType: IndType,
    var indName: String,
    var indParamList: List<Int>
)
