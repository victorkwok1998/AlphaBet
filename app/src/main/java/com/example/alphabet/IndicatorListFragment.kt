package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.alphabet.components.IndicatorList
import com.example.alphabet.components.MyTopAppBar
import com.example.alphabet.ui.theme.grayBackground

class IndicatorListFragment: Fragment() {
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private val viewModel: StrategyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            val indicators = staticDataViewModel.indToParamList.value.keys.toList()
            setContent {
                MaterialTheme {
                    IndicatorListScreen(
                        indicators = indicators,
                        onClick = {
                            with(viewModel.selectedIndicator.value) {
                                indName = indicators[it]
                                val n = staticDataViewModel.indToParamList.value[indName]?.size ?: 0
                                indParamList = MutableList(n) { "" }
                            }
                            findNavController().popBackStack()
                        }
                    )
                }
            }
        }
    }
    
    @Composable
    fun IndicatorListScreen(indicators: List<String>, onClick: (Int) -> Unit) {
        Scaffold(
            topBar = { MyTopAppBar(
                title = { Text("Indicators") },
                navigationIcon = {
                    IconButton(onClick = { findNavController().popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            ) },
        ) {
            Column(
                Modifier
                    .background(grayBackground)
                    .fillMaxSize()
            ) {
                IndicatorList(indicators = indicators, onClick = onClick)
            }
        }
    }
    
    @Preview
    @Composable
    fun PreviewIndicatorListScreen() {
        IndicatorListScreen(indicators = listOf("RSI", "EMA", "SMA"), onClick = {})
    }
}