package com.example.alphabet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class IndicatorStatic(
    val indName: String,
    val indType: IndType,
    val paramName: List<String>
): Parcelable
