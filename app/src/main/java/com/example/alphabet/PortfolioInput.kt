package com.example.alphabet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PortfolioInput(
    val stock: StockStatic,
    var weight: String
) : Parcelable
