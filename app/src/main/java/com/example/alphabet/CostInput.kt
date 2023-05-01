package com.example.alphabet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class CostInput(
    val fee: Float,
    val type: CostType
) : Parcelable

@Parcelize
@Serializable
enum class CostType : Parcelable {
    PCT,
    BPS,
    FIXED
}