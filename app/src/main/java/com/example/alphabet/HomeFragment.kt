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
import com.example.alphabet.components.MyTopAppBar
import com.example.alphabet.ui.theme.MyTheme
import com.example.alphabet.ui.theme.grayBackground
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
                            Json.decodeFromString<Map<String, StrategyInput>>(getJsonDataFromAsset(requireContext(), "defaultStrategy.json"))
                                .toList()

                        staticDataViewModel.radarChartRange.value =
                            Json.decodeFromString(getJsonDataFromAsset(requireContext(), "radarChartRange.json"))
                        requireContext().copyFromAsset("myBacktestResults.json")

                        Json.decodeFromString<Map<Int, BacktestResult>>(
                            readFile(File(requireContext().filesDir, "myBacktestResults.json"))
                        )
                            .forEach {
                                staticDataViewModel.myBacktestResults.add(it.value)
                            }
                    }
                }
                isLoading = false
            }
        }

        Scaffold(
            topBar = { MyTopAppBar(title = { Text("My Backtests") }) },
            content = {
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
                else {
                    Column(
                        Modifier
                            .background(grayBackground)
                    ) {
//                        Text(
//                            "My Backtests",
//                            style = MaterialTheme.typography.h6,
//                            fontWeight = FontWeight.Normal,
//                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
//                        )
//                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            BackTestList(
                                backtestResults = staticDataViewModel.myBacktestResults,
                                modifier = Modifier.padding(20.dp),
                            )
                        }
                    }
                }
            }
        )
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
                                        with(viewModel){
                                            reset()
                                            start.value = stringToCalendar(backtestResult.date.first())
                                            end.value = stringToCalendar(backtestResult.date.last())
                                            symbolStrategyList[0].first.value = backtestResult.symbol
                                            symbolStrategyList[0].second.value = 0
                                            stratIdMap.value = listOf(Pair(backtestResult.strategyName, backtestResult.strategyInput))
                                            val action = HomeFragmentDirections.actionHomeFragmentToBacktestResultFragment()
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
                                "${backtestResult.symbol}, ${backtestResult.strategyName}",
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
                            if (pnl >= 0) colorResource(id = R.color.green)
                            else colorResource(id = R.color.red)
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
                        viewModel.stratIdMap.value = staticDataViewModel.defaultStrategy.value
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