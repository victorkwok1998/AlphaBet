package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.alphabet.components.MyCard
import com.example.alphabet.components.MyTopAppBar
import com.example.alphabet.components.SearchBar
import com.example.alphabet.components.StrategyList
import com.example.alphabet.example.DataExample
import com.example.alphabet.ui.theme.grayBackground

class SelectStrategyFragment: Fragment() {
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private val viewModel: StrategyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    SelectStrategy(
                        staticDataViewModel.defaultStrategy.value,
                    )
                }
            }
        }
    }

    @Composable
    fun SelectStrategy(strategies: List<StrategyInput>) {
        var searchText by remember { mutableStateOf("")}
        val filteredStrategies = if (searchText.isEmpty()) strategies else
            strategies.filter { it.strategyName.lowercase().contains(searchText.lowercase()) }

        Scaffold(
            topBar = {
                MyTopAppBar(
                    title = {
                    SearchBar(value = searchText, onValueChange = { searchText = it })
                },
                    navigationIcon = {
                        IconButton(onClick = { findNavController().popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                )
            },
            content = { paddingValues ->
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(grayBackground)) {
                    MyCard(
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            Modifier
//                            .padding(paddingValues)
                                .verticalScroll(rememberScrollState())
                        ) {
                            StrategyList(
                                filteredStrategies,
                                onOptionSelected = {
                                    with(viewModel) {
                                        symbolStrategyList[inputToSelectStrategy.value].strategyInput = it.copy()
                                    }
                                    findNavController().popBackStack()
                                },
//                        onIconClick = { index ->
////                            viewModel.selectToEditStrategy.value = index
//                            viewModel.symbolStrategyList[viewModel.inputToSelectStrategy.value].strategyInput = staticDataViewModel.defaultStrategy.value[index].copy()
//                            val action = SelectStrategyFragmentDirections.actionSelectStrategyFragmentToEditStrategyFragment()
//                            findNavController().navigate(action)
//                        }
                            )
                        }
                    }
                }

            }
        )


    }

    @Preview
    @Composable
    fun PreviewStrategyRow() {
        val strategies =  listOf(
            DataExample().strategyInput
        )
        SelectStrategy(strategies)
    }
}