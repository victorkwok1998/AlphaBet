package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.alphabet.MyApplication.Companion.sdfISO
import com.example.alphabet.components.MyCard
import com.example.alphabet.components.MyTopAppBar
import com.example.alphabet.components.SearchBar
import com.example.alphabet.components.StrategyList
import com.example.alphabet.ui.theme.MyTheme
import com.example.alphabet.ui.theme.grayBackground
import com.example.alphabet.ui.theme.green500
import com.example.alphabet.ui.theme.red500
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import kotlinx.serialization.encodeToString
import java.util.*

// /data/user/0/com.example.alphabet/files/

class HomeFragment: Fragment()  {
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private val viewModel: StrategyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply { 
            setContent {
                HomeScreen()
            }
        }
    }

    private fun isoToDisplay(dateString: String): String {
        return dateString
            .run { MyApplication.sdfISO.parse(this) }
            .run { MyApplication.sdfLong.format(this!!) }
    }
    
    @Composable
    fun HomeScreen() {
        var isLoading by remember { mutableStateOf(true) }
        LaunchedEffect(key1 = isLoading) {
            this.launch {
                if (staticDataViewModel.indToParamList.value.isEmpty()) {
                    // Read static data
                    withContext(Dispatchers.IO) {
                        staticDataViewModel.indToParamList.value =
                            Json.decodeFromString<Map<String, List<String>>>(getJsonDataFromAsset(requireContext(), "indToParamList.json"))
                        staticDataViewModel.defaultStrategy.value =
                            Json.decodeFromString<List<StrategyInput>>(getJsonDataFromAsset(requireContext(), "defaultStrategy.json"))
                                .sortedBy { it.strategyName }

                        staticDataViewModel.radarChartRange.value =
                            Json.decodeFromString(getJsonDataFromAsset(requireContext(), "radarChartRange.json"))
                        requireContext().copyFromAsset("myBacktestResults.json")

                        Json.decodeFromString<List<BacktestResult>>(
                            readFile(File(requireContext().filesDir, "myBacktestResults.json"))
                        )
                            .forEach {
                                staticDataViewModel.myBacktestResults.add(it)
                            }
                    }
                }
                isLoading = false
            }
        }
        var selectedItem by remember { mutableStateOf(0)}
        var searchText by remember { mutableStateOf("") }
        val filteredStrategies =
            if (searchText.isEmpty()) staticDataViewModel.defaultStrategy.value else staticDataViewModel.defaultStrategy.value.filter {
                it.strategyName.lowercase().contains(searchText.lowercase())
            }
        Scaffold(
            topBar = {
                if(selectedItem == 0)
                    MyTopAppBar(title = { Text("AlphaBet") })
                else{
                    MyTopAppBar {
                        SearchBar(
                            value = searchText,
                            onValueChange = { searchText = it },
                        )
                    }
                }

            },
            content = { paddingValues ->
                Column(
                    Modifier
                        .padding(paddingValues = paddingValues)
                ) {
                    if (isLoading){
                        Column(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(10.dp))
                            Text("Loading", color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium))
                        }
                    }
                    else if (selectedItem == 0) {
                        MyBacktestScreen()
                    }
                    else if (selectedItem == 1) {
                        MyStrategyScreen(filteredStrategies)
                    }
                }
            },
            bottomBar = {
                if (!isLoading) {
                    BottomNavigation(backgroundColor = MaterialTheme.colors.background) {
                        listOf("Backtest", "Stratgey")
                            .zip(listOf(R.drawable.ic_baseline_description_24, R.drawable.ic_baseline_emoji_objects_24))
                            .forEachIndexed { index, (item, icon) ->
                                val selectedColor =
                                    if (selectedItem == index) MaterialTheme.colors.primary else Color.Gray
                                BottomNavigationItem(
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = icon),
                                            contentDescription = null,
                                            tint = selectedColor
                                        )
                                    },
                                    label = { Text(item, color = selectedColor) },
                                    selected = selectedItem == index,
                                    onClick = {
                                        selectedItem = index
                                        //                                val action = HomeFragmentDirections.actionHomeFragmentToSelectStrategyFragment()
                                        //                                findNavController().navigate(action)
                                    })
                            }
                    }
                }
            }
        )
    }

    @Composable
    fun MyBacktestScreen() {
        Column(
            Modifier
                .background(grayBackground)
        ) {
            MyCard(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                BackTestList(
                    backtestResults = staticDataViewModel.myBacktestResults,
                    modifier = Modifier.padding(20.dp),
                )
            }
        }
    }

    @Composable
    fun MyStrategyScreen(strategies: List<StrategyInput>) {
        Column(
            Modifier
                .background(grayBackground)
        ) {
            MyCard {
                Column(
                    Modifier
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState())) {
                    StrategyList(strategies = strategies, onOptionSelected = {})
                }
            }
        }
    }

    @Composable
    fun BackTestList(
        backtestResults: SnapshotStateList<BacktestResult>,
        modifier: Modifier = Modifier,
    ) {
        val weights = listOf(0.65f, 0.25f, 0.1f)
        Column(modifier) {
            // header
            Row {
                Text(
                    "Backtest",
                    modifier = Modifier.weight(weights[0]),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Return",
                    modifier = Modifier.weight(weights[1]),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(weights[2]))
            }
            // list
            Column(
                Modifier
                    .verticalScroll(rememberScrollState()))
            {
                backtestResults.forEachIndexed { index, backtestResult ->
                    Row(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .pointerInput(Unit)
                            {
                                detectTapGestures(
                                    onTap = {
                                        with(viewModel) {
                                            reset()
                                            start.value =
                                                stringToCalendar(backtestResult.date.first())
                                            end.value = stringToCalendar(backtestResult.date.last())
                                            symbolStrategyList[0].symbol.value =
                                                backtestResult.symbol
                                            symbolStrategyList[0].strategyInput =
                                                backtestResult.strategyInput
//                                            stratIdMap.value = listOf(
//                                                Pair(
//                                                    backtestResult.strategyName,
//                                                    backtestResult.strategyInput
//                                                )
//                                            )
                                            val action =
                                                HomeFragmentDirections.actionHomeFragmentToBacktestResultFragment()
                                            findNavController().navigate(action)
                                        }
                                    },
                                    onLongPress = {}
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(weights[0])) {
                            Text(
                                "${backtestResult.symbol}, ${backtestResult.strategyInput.strategyName}",
                                style = MaterialTheme.typography.subtitle1
                            )
                            Text(
                                "${isoToDisplay(backtestResult.date.first())} - ${isoToDisplay(backtestResult.date.last())}",
                                style = MaterialTheme.typography.caption,
                                color = Color.Unspecified.copy(alpha = 0.6f)
                            )
                        }
                        val pnl = backtestResult.cashFlow.last() - 1
                        val color =
                            if (pnl >= 0) green500
                            else red500
                        Column(Modifier.weight(weights[1])) {
                            Text(
                                MyApplication.pct.format(pnl),
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .background(color)
                                    .padding(vertical = 2.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.subtitle1,
                                textAlign = TextAlign.Center
                            )
                        }
                        IconButton(onClick = {
                            backtestResults.removeAt(index)
                            backtestResults
                                .mapIndexed { index, backtestResult -> index to backtestResult }
                                .toMap()
                                .apply {
                                    File(requireContext().filesDir, "myBacktestResults.json")
                                        .writeText(Json.encodeToString(this))
                                }
                        },modifier = Modifier.weight(weights[2])) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_cancel_24),
                                contentDescription = "",
                                tint = Color.LightGray
                            )
                        }
                    }
                }
                if (backtestResults.isEmpty()){
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "No backtest yet. \nLet's create your own backtest!",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                // button
                OutlinedButton(
                    onClick = {
//                        viewModel.stratIdMap.value = staticDataViewModel.defaultStrategy.value
                        viewModel.reset()
                        val action = HomeFragmentDirections.actionHomeFragmentToBacktestInputFragment()
                        findNavController().navigate(action)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create a Backtest", style = MaterialTheme.typography.subtitle1)
                }
            }
        }
    }
}