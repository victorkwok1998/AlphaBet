package com.example.alphabet

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioInput(
    val stock: StockStatic,
    var weight: String
)
