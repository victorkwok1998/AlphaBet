package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
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
//                val selected = staticDataViewModel.defaultStrategy.value
//                    .indexOfFirst { it.first == viewModel.stratName.value }
//                    .run { max(this, 0) }
                SelectStrategy(
                    staticDataViewModel.defaultStrategy.value,
//                    viewModel.symbolStrategyList[viewModel.inputToSelectStrategy.value].second.value
                )
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
                MyTopAppBar {
                    SearchBar(value = searchText, onValueChange = { searchText = it })
                }
            },
            content = { paddingValues ->
                Column(Modifier
                    .fillMaxSize()
                    .background(grayBackground)) {
                    MyCard(
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            Modifier
                                .padding(horizontal = 20.dp)
//                            .padding(paddingValues)
                                .verticalScroll(rememberScrollState())
                        ) {
                            StrategyList(
                                filteredStrategies,
                                onOptionSelected = {
                                    viewModel.symbolStrategyList[viewModel.inputToSelectStrategy.value].strategyInput = staticDataViewModel.defaultStrategy.value[it].copy()
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