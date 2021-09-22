package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.alphabet.components.MyTopAppBar
import com.example.alphabet.example.DataExample

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
                    viewModel.symbolStrategyList[viewModel.inputToSelectStrategy.value].second.value
                )
            }
        }
    }

    @Composable
    fun StrategyList(strategies: List<Pair<String, StrategyInput>>, selected: Int, onOptionSelected: (Int) -> Unit) {
        var selectedOption by remember { mutableStateOf(selected)}
        Column {
            strategies.forEachIndexed { index, pair ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (index == selectedOption),
                            onClick = {
                                selectedOption = index
                                onOptionSelected(index)
                            },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .weight(0.05f),
                    horizontalAlignment = Alignment.CenterHorizontally) {
//                        RadioButton(selected = index == selectedOption, onClick = null)
                    }
                    Column(
                        Modifier.weight(0.8f)
                    ) {
                        Text(
                            text = pair.first,
                            style = MaterialTheme.typography.body1,
                            fontSize = 18.sp
//                            fontWeight = FontWeight.Bold,
                        )
//                        Spacer(modifier = Modifier.padding(vertical = 3.dp))
//                        Text(
//                            text = pair.second.des,
//                            style = MaterialTheme.typography.body1
//                        )
                    }
                    Column(Modifier.weight(0.15f),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = {
                            viewModel.selectToEditStrategy.value = index
                            val action = SelectStrategyFragmentDirections.actionSelectStrategyFragmentToEditStrategyFragment()
                            findNavController().navigate(action)
                        },) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "",
                                tint = Color.LightGray
                            )
                        }
                    }
                }
                if (index < strategies.lastIndex)
                    Divider(modifier = Modifier.padding(vertical = 10.dp), thickness = 1.dp)
            }
        }

    }
    
    @Composable
    fun SelectStrategy(strategies: List<Pair<String, StrategyInput>>, selected: Int) {
        Scaffold(
            topBar = {
                MyTopAppBar(
                    title = { Text("Strategy List") },
                    navigationIcon = {
                        IconButton(onClick = {
                            findNavController().popBackStack()
                        }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    })
            },
//            bottomBar = { BottomAppBar {
//                BottomBarItem(icon = R.drawable.ic_baseline_add_24, text = "Create", onClick = {  })
//                BottomBarItem(
//                    icon = R.drawable.ic_baseline_check_24,
//                    text = "Done",
//                    onClick = {
//                        with(viewModel) {
//                            symbolStrategyList[inputToSelectStrategy.value].second.value = strategies[selectedOption].first
//                        }
//                        val action = SelectStrategyFragmentDirections.actionSelectStrategyFragmentToBacktestInputFragment()
//                        findNavController().navigate(action)
//                    })
//            } },
            content = { paddingValues ->
                Column(
                    Modifier
                        .padding(10.dp)
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    StrategyList(strategies, selected = selected, onOptionSelected = {
                        viewModel.symbolStrategyList[viewModel.inputToSelectStrategy.value].second.value = it
                        findNavController().popBackStack()
                    })
                }
            }
        )


    }

    @Preview
    @Composable
    fun PreviewStrategyRow() {
        val strategies =  listOf(
            Pair("RSI Strategy", DataExample().strategyInput)
        )
        SelectStrategy(strategies, 0)
    }
}