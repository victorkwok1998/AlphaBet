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
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yahoofinance.YahooFinance
import java.io.FileNotFoundException
import java.util.*
import com.example.alphabet.MyApplication.Companion.sdfLong
import com.example.alphabet.components.*
import com.example.alphabet.ui.theme.amber500
import com.example.alphabet.ui.theme.grayBackground
import java.net.SocketTimeoutException

//TODO: Recent Backtests
class BacktestInputFragment: Fragment(R.layout.fragment_backtest_input) {
    private val viewModel: StrategyViewModel by activityViewModels()
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private val symbolStrategyWeight = listOf(0.85f, 0.15f)

    @ExperimentalComposeUiApi
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
                MaterialTheme {
                    BacktestInputScreen()
                }
            }
        }
    }

    @ExperimentalComposeUiApi
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
                    SymbolStrategyCard(viewModel.symbolStrategyList) { viewModel.addEmptyStrategy() }
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

    @ExperimentalComposeUiApi
    @Composable
    fun SymbolStrategyCard(
        symbolStrategyList: SnapshotStateList<BacktestInput>,
        onAddClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = tween(easing = LinearOutSlowInEasing, durationMillis = 300)
                ),
            shape = RoundedCornerShape(20.dp),
            elevation = 0.dp
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Symbol and Strategy",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.weight(symbolStrategyWeight[0])
                    )
                    IconButton(onClick = onAddClick, modifier = Modifier.weight(symbolStrategyWeight[1])) {
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
        MyCard(modifier = Modifier
            .fillMaxWidth(),
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Date Range", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth()) {
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
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    listOf(1, 2, 5, 10).forEach { year ->
                        DefaultTimeButton(year = year)
                        Spacer(modifier = Modifier.width(15.dp))
                    }
                }
            }
        }
    }

    @Composable
    fun DefaultTimeButton(year: Int) {
        OutlinedButton(onClick = {
            viewModel.setDateRange(year)
        }) {
            Text("$year Y")
        }
    }

    @Composable
    fun DateTextField(label: String, cal: MutableState<Calendar>) {
        val c = cal.value
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        ClickableTextField(value = sdfLong.format(c.time),
//            onValueChange = {},
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_date_range_24),
                    contentDescription = "date",
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
                }
        )
    }


    @ExperimentalComposeUiApi
    @Composable
    fun SymbolStrategyRow(index: Int) {
        val backtestInput = viewModel.symbolStrategyList[index]
        val symbol = backtestInput.symbol
        val strategy = backtestInput.strategyInput
        Row(Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(symbolStrategyWeight[0])) {
                MyTextField(
                    value = symbol.value,
                    onValueChange = { symbol.value = it },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_timeline_24),
                            contentDescription = "Symbol",
                            tint = Color.LightGray
                        )
                    },
                    trailingIcon = {
                        if (symbol.value.isNotEmpty()) {
                            IconButton(onClick = { symbol.value = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    label = { Text("Symbol") }
                )
//                OutlinedTextField(
//                    value = symbol.value,
//                    onValueChange = { symbol.value = it },
//                    label = { Text("Symbol") },
//                    leadingIcon = {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_baseline_timeline_24),
//                            contentDescription = "Symbol",
//                        )
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                )
                // hint text
                Text(
                    "e.g., TSLA, BRK-B, BTC-USD, 2800.HK",
                    modifier = Modifier.padding(start = 5.dp),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                )
                Spacer(modifier = Modifier.height(10.dp))
                ClickableTextField(
                    value = if(strategy.isEmpty()) "" else strategy.strategyName,
                    label = if(strategy.isEmpty()) null else {{Text("Strategy")}},
                    placeholder = { Text("Select a Strategy") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_emoji_objects_24),
                            contentDescription = "Strategy",
                            tint = if(strategy.isEmpty()) Color.LightGray else amber500
                        )
                    },
                    trailingIcon = {
                        if(!strategy.isEmpty()){
                            IconButton(onClick = {
                                viewModel.inputToSelectStrategy.value = index
                                val action = BacktestInputFragmentDirections.actionBacktestInputFragmentToEditStrategyFragment()
                                findNavController().navigate(action)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = Color.LightGray
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color.LightGray
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.inputToSelectStrategy.value = index
                            val action =
                                BacktestInputFragmentDirections.actionBacktestInputFragmentToSelectStrategyFragment()
                            findNavController().navigate(action)
                        },
                )
//                ClickableOutlinedTextField(
//                    value = if(strategy.isEmpty()) "" else strategy.strategyName,
//                    onValueChange = {  },
//                    label = { Text("Strategy") },
//                    placeholder = { Text("Select a Strategy") },
//                    leadingIcon = {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_baseline_emoji_objects_24),
//                            contentDescription = "Strategy",
//                        )
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {
//                            viewModel.inputToSelectStrategy.value = index
//                            val action =
//                                BacktestInputFragmentDirections.actionBacktestInputFragmentToSelectStrategyFragment()
//                            findNavController().navigate(action)
//                        }
//                )
            }
            if (viewModel.symbolStrategyList.size > 1) {
                IconButton(
                    onClick = {
                        viewModel.symbolStrategyList.removeAt(index)
                    },
                    modifier = Modifier.weight(symbolStrategyWeight[1]),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_cancel_24),
                        contentDescription = "Cancel",
                        tint = Color.Gray
                    )
                }
            }
            else {
                Spacer(modifier = Modifier.weight(symbolStrategyWeight[1]))
            }
        }
    }
    

    private fun setDatePicker(editText: EditText, c: Calendar) {
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        if (editText.text.toString().isEmpty()) {
            editText.setText(sdfLong.format(c.time))
        }

        editText.setOnClickListener {
            val dpd = DatePickerDialog(requireContext(), R.style.MySpinnerDatePickerStyle, { view, mYear, mMonth, mDay ->
                val cal = createCalandar(mYear, mMonth, mDay)
                editText.setText(sdfLong.format(cal.time))
            }, year, month, day)
            dpd.show()
        }
    }

    private fun processUserInput() {
        lifecycleScope.launch {
            val symbols = viewModel.symbolStrategyList.map { it.symbol.value }.toSet()
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
                } catch (e: SocketTimeoutException) {
                    Toast.makeText(context, "Please Check Internet Connection", Toast.LENGTH_LONG).show()
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