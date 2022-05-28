package com.example.alphabet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.ta4j.core.Trade

@Parcelize
@Serializable
data class TradeData(
    val tradeType: Trade.TradeType,
    val index: Int,
    val pricePerAsset: Float,
): Parcelable
