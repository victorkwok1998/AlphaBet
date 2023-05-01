package com.example.alphabet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Metrics(
    val pnl: Float,
    val pnlPct: Float,
    val profitCount: Int,
    val lossCount: Int,
    val nTrade: Int,
    val winRate: Float,
    val mdd: Float,
    val profitFactor: Float,
    val vol: Float
): Parcelable
