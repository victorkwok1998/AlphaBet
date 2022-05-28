package com.example.alphabet.viewmodel

import androidx.lifecycle.ViewModel
import com.example.alphabet.PortfolioInput
import com.example.alphabet.database.PortfolioResultSchema

class PortfolioViewModel: ViewModel() {
    val symbolWeightingMap = mutableMapOf<String, PortfolioInput>()
}