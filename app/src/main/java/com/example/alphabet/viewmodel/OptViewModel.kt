package com.example.alphabet.viewmodel

import androidx.lifecycle.ViewModel
import com.example.alphabet.databinding.LayoutOptParamRowBinding

class OptViewModel: ViewModel() {
    val paramRowList = mutableListOf<LayoutOptParamRowBinding>()
}