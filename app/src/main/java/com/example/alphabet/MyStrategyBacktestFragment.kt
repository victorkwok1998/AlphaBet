package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.databinding.BacktestResultRowBottomSheetBinding
import com.example.alphabet.databinding.FragmentMyStrategyBacktestBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MyStrategyBacktestFragment : Fragment(), BacktestResultAdapter.OnItemClickListener {
    private lateinit var navController: NavController
    private lateinit var databaseViewModel: DatabaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Parent level nav controller
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        databaseViewModel = ViewModelProvider(requireActivity()).get(DatabaseViewModel::class.java)
        val binding = FragmentMyStrategyBacktestBinding.inflate(inflater, container, false)

        val adapter = BacktestResultAdapter(requireContext(), this,
//            onDeleteClicked = { backtestResultId ->
//                databaseViewModel.deleteBacktestResult(backtestResultId)},
//            onRerunClicked = {}
        )

        databaseViewModel.readAllBacktestResultData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.viewEmptyBacktest.root.isVisible = it.isEmpty()
        }
        binding.myBacktestList.adapter = adapter
        binding.myBacktestList.layoutManager = LinearLayoutManager(requireContext())

        binding.viewEmptyBacktest.buttonEmptyList.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToBacktestInputFragment(arrayOf(), arrayOf())
            navController.navigate(action)
        }

        return binding.root
    }

    override fun onItemClick(v: View, position: Int) {
        databaseViewModel.readAllBacktestResultData.value?.let {
            val backtestResult = it[position]
            val action = HomeFragmentDirections.actionHomeFragmentToBacktestResultFragment(
                arrayOf(backtestResult.backtestResult))
            navController.navigate(action)
        }
    }

    override fun onItemLongClick(position: Int) {
        val currentItem = databaseViewModel.readAllBacktestResultData.value!![position]
        BottomSheetDialog(requireContext()).apply {
            val binding = BacktestResultRowBottomSheetBinding.inflate(layoutInflater, null, false)

            binding.backtestInfo.symbolText.text = currentItem.backtestResult.backtestInput.stock.symbol
            binding.backtestInfo.strategyNameText.text = currentItem.backtestResult.backtestInput.strategyInput.strategy.strategyName
            binding.backtestInfo.dateRangeText.text = getBacktestPeriodString(currentItem.backtestResult, requireContext())

            binding.deleteRow.setOnClickListener {
                databaseViewModel.deleteBacktestResult(currentItem)
                this.dismiss()
            }
            binding.rerunRow.setOnClickListener {
                val start = currentItem.backtestResult.date.first().toCalendar()
                val end = getYesterday()
                val action = HomeFragmentDirections.actionHomeFragmentToRunStrategyDialog(
                    start, end, arrayOf(currentItem.backtestResult.backtestInput.strategyInput), arrayOf(currentItem.backtestResult.backtestInput.stock), CostInput(0f, CostType.BPS))
                navController.navigate(action)
                this.dismiss()
            }
            binding.shareRow.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToShareBacktestFragment(currentItem.backtestResult)
                navController.navigate(action)
                this.dismiss()
            }
            binding.optimizeRow.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToOptParamInputFragment(
                    start = currentItem.backtestResult.date.first().toCalendar(),
                    end = currentItem.backtestResult.date.last().toCalendar(),
                    stock = currentItem.backtestResult.backtestInput.stock,
                    strategy = currentItem.backtestResult.backtestInput.strategyInput.strategy
                )
                navController.navigate(action)
                this.dismiss()
            }
            this.setContentView(binding.root)
            this.show()
        }
    }

//    @Composable
//    fun MyBacktestScreen() {
//        Column(
//            Modifier
//                .background(grayBackground)
//        ) {
//            MyCard(
//                modifier = Modifier.fillMaxSize(),
//                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
//            ) {
//                BackTestList(
//                    backtestResults = staticDataViewModel.myBacktestResults,
//                    modifier = Modifier.padding(20.dp),
//                )
//            }
//        }
//    }
//
//    @Composable
//    fun BackTestList(
//        backtestResults: SnapshotStateList<BacktestResult>,
//        modifier: Modifier = Modifier,
//    ) {
//        val weights = listOf(0.65f, 0.25f, 0.1f)
//        Column(modifier) {
//            // header
//            Row {
//                Text(
//                    "Backtest",
//                    modifier = Modifier.weight(weights[0]),
//                    style = MaterialTheme.typography.subtitle1,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    "Return",
//                    modifier = Modifier.weight(weights[1]),
//                    style = MaterialTheme.typography.subtitle1,
//                    fontWeight = FontWeight.Bold
//                )
//                Spacer(modifier = Modifier.weight(weights[2]))
//            }
//            // list
//            Column(
//                Modifier
//                    .verticalScroll(rememberScrollState()))
//            {
//                backtestResults.forEachIndexed { index, backtestResult ->
//                    Row(
//                        modifier = Modifier
//                            .padding(vertical = 10.dp)
//                            .pointerInput(Unit)
//                            {
//                                detectTapGestures(
//                                    onTap = {
//                                        with(viewModel) {
//                                            reset()
//                                            start.value =
//                                                stringToCalendar(backtestResult.date.first())
//                                            end.value = stringToCalendar(backtestResult.date.last())
//                                            symbolStrategyList[0].symbol =
//                                                backtestResult.symbol
//                                            symbolStrategyList[0].strategyInput =
//                                                backtestResult.strategyInput
////                                            stratIdMap.value = listOf(
////                                                Pair(
////                                                    backtestResult.strategyName,
////                                                    backtestResult.strategyInput
////                                                )
////                                            )
//                                            val action =
//                                                HomeFragmentDirections.actionHomeFragmentToBacktestResultFragment()
//                                            navController.navigate(action)
//                                        }
//                                    },
//                                    onLongPress = {}
//                                )
//                            },
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Column(Modifier.weight(weights[0])) {
//                            Text(
//                                buildAnnotatedString {
//                                    append(backtestResult.symbol)
//                                    append(", ")
//                                    withStyle(style = SpanStyle(color = Color.Gray, fontSize = 14.sp)) {
//                                        append(backtestResult.strategyInput.strategyName)
//                                    }
//                                },
////                                "${backtestResult.symbol}, ${backtestResult.strategyInput.strategyName}",
////                                style = MaterialTheme.typography.subtitle1
//                            )
//                            Text(
//                                "${isoToDisplay(backtestResult.date.first())} - ${isoToDisplay(backtestResult.date.last())}",
//                                style = MaterialTheme.typography.caption,
//                                color = Color.Unspecified.copy(alpha = 0.6f)
//                            )
//                        }
//                        val pnl = backtestResult.cashFlow.last() - 1
//                        val color =
//                            if (pnl >= 0) green500
//                            else red500
//                        Column(Modifier.weight(weights[1])) {
//                            Text(
//                                MyApplication.pct.format(pnl),
//                                modifier = Modifier
//                                    .fillMaxWidth(0.9f)
//                                    .background(color)
//                                    .padding(vertical = 2.dp),
//                                color = Color.White,
//                                style = MaterialTheme.typography.subtitle1,
//                                textAlign = TextAlign.Center
//                            )
//                        }
//                        IconButton(onClick = {
//                            backtestResults.removeAt(index)
//                            backtestResults
//                                .mapIndexed { index, backtestResult -> index to backtestResult }
//                                .toMap()
//                                .apply {
//                                    File(requireContext().filesDir, "myBacktestResults.json")
//                                        .writeText(Json.encodeToString(this))
//                                }
//                        },modifier = Modifier.weight(weights[2])) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.ic_baseline_cancel_24),
//                                contentDescription = "",
//                                tint = Color.LightGray
//                            )
//                        }
//                    }
//                }
////                if (backtestResults.isEmpty()){
////                    Spacer(modifier = Modifier.height(10.dp))
////                    Text(
////                        "No backtest yet. \nLet's create your own backtest!",
////                        style = MaterialTheme.typography.subtitle1,
////                        color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
////                    )
////                }
////                Spacer(modifier = Modifier.height(10.dp))
////                // button
////                OutlinedButton(
////                    onClick = {
//////                        viewModel.stratIdMap.value = staticDataViewModel.defaultStrategy.value
////                        viewModel.reset()
////                        val action = HomeFragmentDirections.actionHomeFragmentToBacktestInputFragment()
////                        navController.navigate(action)
////                    },
////                    modifier = Modifier
////                        .fillMaxWidth()
////                        .height(45.dp)
////                ) {
////                    Text("Create a Backtest", style = MaterialTheme.typography.subtitle1)
////                }
//            }
//        }
//    }
}