package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.components.MyCard
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.databinding.BacktestResultRowBottomSheetBinding
import com.example.alphabet.databinding.FragmentMyBacktestBinding
import com.example.alphabet.ui.theme.grayBackground
import com.example.alphabet.ui.theme.green500
import com.example.alphabet.ui.theme.red500
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class MyBacktestFragment : Fragment(), BacktestResultAdapter.OnItemClickListener {
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private val viewModel: StrategyViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var databaseViewModel: DatabaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Parent level nav controller
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
//        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)
        databaseViewModel = ViewModelProvider(requireActivity()).get(DatabaseViewModel::class.java)
        val binding = FragmentMyBacktestBinding.inflate(inflater, container, false)

        val adapter = BacktestResultAdapter(requireContext(),
            this,
            onDeleteClicked = { backtestResultId ->
                databaseViewModel.deleteBacktestResult(backtestResultId)},
            onRerunClicked = {}
        )

        databaseViewModel.readAllBacktestResultData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if (it.isEmpty()) {
                binding.groupEmpty.visibility = View.VISIBLE
            } else {
                binding.groupEmpty.visibility = View.GONE
            }
        }
        binding.myBacktestList.adapter = adapter
        binding.myBacktestList.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
//        return ComposeView(requireContext()).apply {
//            setContent {
//                MaterialTheme() {
//                    MyBacktestScreen()
//                }
//            }
//        }
    }

    override fun onItemClick(position: Int) {
        databaseViewModel.readAllBacktestResultData.value?.let {
            val backtestResult = it[position]
            with(viewModel) {
                reset()
                start.value = stringToCalendar(backtestResult.backtestResult.date.first())
                end.value = stringToCalendar(backtestResult.backtestResult.date.last())
//                symbolStrategyList[0].symbol = backtestResult.backtestResult.symbol
//                symbolStrategyList[0].strategyInput = backtestResult.backtestResult.strategyInput
                symbolStrategyList.add(BacktestInput(backtestResult.backtestResult.symbol, backtestResult.backtestResult.strategyInput))
                val action = HomeFragmentDirections.actionHomeFragmentToBacktestResultFragment()
                navController.navigate(action)
            }
        }
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
                                            symbolStrategyList[0].symbol =
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
                                            navController.navigate(action)
                                        }
                                    },
                                    onLongPress = {}
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(weights[0])) {
                            Text(
                                buildAnnotatedString {
                                    append(backtestResult.symbol)
                                    append(", ")
                                    withStyle(style = SpanStyle(color = Color.Gray, fontSize = 14.sp)) {
                                        append(backtestResult.strategyInput.strategyName)
                                    }
                                },
//                                "${backtestResult.symbol}, ${backtestResult.strategyInput.strategyName}",
//                                style = MaterialTheme.typography.subtitle1
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
//                if (backtestResults.isEmpty()){
//                    Spacer(modifier = Modifier.height(10.dp))
//                    Text(
//                        "No backtest yet. \nLet's create your own backtest!",
//                        style = MaterialTheme.typography.subtitle1,
//                        color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
//                    )
//                }
//                Spacer(modifier = Modifier.height(10.dp))
//                // button
//                OutlinedButton(
//                    onClick = {
////                        viewModel.stratIdMap.value = staticDataViewModel.defaultStrategy.value
//                        viewModel.reset()
//                        val action = HomeFragmentDirections.actionHomeFragmentToBacktestInputFragment()
//                        navController.navigate(action)
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(45.dp)
//                ) {
//                    Text("Create a Backtest", style = MaterialTheme.typography.subtitle1)
//                }
            }
        }
    }
}