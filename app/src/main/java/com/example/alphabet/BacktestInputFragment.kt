package com.example.alphabet

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yahoofinance.YahooFinance
import java.io.FileNotFoundException
import java.util.*
import com.example.alphabet.MyApplication.Companion.sdfLong
import com.example.alphabet.components.ClickableOutlinedTextField
import com.example.alphabet.components.MyTopAppBar
import com.example.alphabet.ui.theme.grayBackground

//TODO: Recent Backtests
class BacktestInputFragment: Fragment(R.layout.fragment_backtest_input) {
    private val viewModel: StrategyViewModel by activityViewModels()
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        _binding = FragmentBacktestInputBinding.inflate(inflater, container, false)
//
//        val defaultEnd = Calendar.getInstance().apply {add(Calendar.DATE, -1)}  // yesterday
//        setDatePicker(binding.endDate, defaultEnd)
//
//        val defaultStart = defaultEnd.apply { add(Calendar.YEAR, -1) }  // one year before
//        setDatePicker(binding.startDate, defaultStart)
//
//        binding.strategyText.apply {
//            setText(viewModel.stratName.value.run {
//                when {
//                    this.isEmpty() -> ""
//                    else -> "$this Strategy"
//                }
//            })
//            setOnClickListener {
//                viewModel.symbol.value = binding.symbol.text.toString()
//                val action = BacktestInputFragmentDirections.actionBacktestInputFragmentToSelectStrategyFragment()
//                findNavController().navigate(action)
//            }
//        }
//
//        binding.symbol.setText(viewModel.symbol.value)
//
////        val strategyNameList = StrategyName.values().map { requireContext().getString(R.string.strategy_name, it.fullName) }
//
////        val adapter = ArrayAdapter(requireContext(), R.layout.strategy_list_item, strategyNameList)
////        val actv = binding.strategyListDropdown.editText as AutoCompleteTextView
//
////        actv.setAdapter(adapter)
////        selectedStrategyName = StrategyName.values().first()
////        actv.setText(strategyNameList.first(), false)
////        setStrategyDes(selectedStrategyName)
////        actv.setOnItemClickListener { parent, view, position, id ->
////            selectedStrategyName = StrategyName.values()[position]
////            setStrategyDes(selectedStrategyName)
////            binding.backtestButton.visibility = if (selectedStrategyName == StrategyName.CUSTOM) View.GONE else View.VISIBLE
////        }
//
//        binding.backtestButton.setOnClickListener {
//            processUserInput()
//        }
////        binding.advOptsButton.setOnClickListener {
////            processUserInput(true)
////        }
//
////        return binding.root
        return ComposeView(requireContext()).apply {
            setContent {
                BacktestInputScreen()
            }
        }
    }
    
    @Composable
    fun BacktestInputScreen() {
        Scaffold(
            topBar = {
                        MyTopAppBar(
                            title = { Text("Create") },
                            navigationIcon = {
                                IconButton(onClick = { findNavController().popBackStack() }) {
                                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                                }
                            }
                        )
                     },
            content = {
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(grayBackground)
                        .verticalScroll(rememberScrollState())
                ) {
//                    Spacer(modifier = Modifier.height(10.dp))
                    SymbolStrategyCard(viewModel.symbolStrategyList)
                    Spacer(modifier = Modifier.height(10.dp))
                    DateRangeCard()
                    Button(onClick = {
                        processUserInput()
                    },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(10.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("BACKTEST")
                    }
                }
            }
        )
    }

    @Composable
    fun SymbolStrategyCard(
        symbolStrategyList: SnapshotStateList<Pair<MutableState<String>, MutableState<Int>>>
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = tween(easing = LinearOutSlowInEasing, durationMillis = 300)
                ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Symbol and Strategy",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.weight(0.8f)
                    )
                    IconButton(onClick = {
                        symbolStrategyList.add(Pair(mutableStateOf(""), mutableStateOf(-1)))
                    }, modifier = Modifier.weight(0.2f)) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add symbol and strategy"
                        )
                    }
                }
                for (i in symbolStrategyList.indices) {
                    SymbolStrategyRow(index = i)
                }
            }
        }
    }

    @Composable
    fun DateRangeCard() {
        Card(modifier = Modifier
            .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Date Range", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    Modifier
                        .fillMaxWidth()) {
                    Column(
                        Modifier
                            .weight(0.5f)
                            .padding(end = 10.dp)) {
                        DateTextField(label = "Start Date", cal = viewModel.start)
                    }
                    Column(
                        Modifier
                            .weight(0.5f)
                            .padding(start = 10.dp)) {
                        DateTextField(label = "End Date", cal = viewModel.end)
                    }
                }
            }
        }
    }

    @Composable
    fun DateTextField(label: String, cal: MutableState<Calendar>) {
        val c = cal.value
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        ClickableOutlinedTextField(value = sdfLong.format(c.time),
            onValueChange = {},
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_date_range_24),
                    contentDescription = "date"
                )
            },
            modifier = Modifier
                .clickable {
                    val dpd = DatePickerDialog(
                        requireContext(),
                        R.style.MySpinnerDatePickerStyle,
                        { view, mYear, mMonth, mDay ->
                            cal.value = createCalandar(mYear, mMonth, mDay)
                        },
                        year,
                        month,
                        day
                    )
                    dpd.show()
                })
    }


    @Composable
    fun SymbolStrategyRow(index: Int) {
        val symbolStrategyPair = viewModel.symbolStrategyList[index]
        val symbol = symbolStrategyPair.first
        val strategy = symbolStrategyPair.second
        Row(Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(0.8f)) {
                OutlinedTextField(
                    value = symbol.value,
                    onValueChange = { symbol.value = it },
                    label = { Text("Symbol") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_trending_up_24),
                            contentDescription = "Symbol"
                        )
                    },
                )
                // hint text
                Text(
                    "e.g., TSLA, BRK-B, BTC-USD, 2800.HK",
                    modifier = Modifier.padding(start = 5.dp),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                )
                ClickableOutlinedTextField(
                    value = if(strategy.value != -1) staticDataViewModel.defaultStrategy.value[strategy.value].first else "",
                    onValueChange = {  },
                    label = { Text("Strategy") },
                    placeholder = { Text("Select a Strategy") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_emoji_objects_24),
                            contentDescription = "Strategy"
                        )
                    },
                    modifier = Modifier.clickable {
                        viewModel.inputToSelectStrategy.value = index
                        val action = BacktestInputFragmentDirections.actionBacktestInputFragmentToSelectStrategyFragment()
                        findNavController().navigate(action)
                    }
                )
            }
            if (viewModel.symbolStrategyList.size > 1) {
                IconButton(
                    onClick = {
                        viewModel.symbolStrategyList.removeAt(index)
                    },
                    modifier = Modifier.weight(0.2f),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_cancel_24),
                        contentDescription = "Cancel",
                        tint = Color.Gray
                    )
                }
            }
            else {
                Spacer(modifier = Modifier.weight(0.2f))
            }
        }
    }
    

    private fun setDatePicker(editText: EditText, c: Calendar) {
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        if (editText.text.toString().isEmpty()) {
            editText.setText(MyApplication.sdfLong.format(c.time))
        }

        editText.setOnClickListener {
            val dpd = DatePickerDialog(requireContext(), R.style.MySpinnerDatePickerStyle, { view, mYear, mMonth, mDay ->
                val cal = createCalandar(mYear, mMonth, mDay)
                editText.setText(MyApplication.sdfLong.format(cal.time))
            }, year, month, day)
            dpd.show()
        }
    }

    private fun processUserInput() {
        lifecycleScope.launch {
            val symbols = viewModel.symbolStrategyList.map { it.first.value }.toSet()
            val isValid = symbols.map { it to false }.toMap().toMutableMap()
            for (symbol in symbols) {
                try {
                    withContext(Dispatchers.IO) {
                        YahooFinance.get(symbol)
                        isValid[symbol] = true
                    }
                } catch (e: FileNotFoundException) {
                    Log.e("YahooFinance", "Failed to get stock $symbol")
                    Toast.makeText(context, "Invalid Symbol", Toast.LENGTH_LONG).show()
                    break
                }
            }
            if (isValid.values.all { it }) {
                val action = com.example.alphabet.BacktestInputFragmentDirections.actionBacktestInputFragmentToBacktestResultFragment()
                findNavController().navigate(action)
            }
        }
    }
}