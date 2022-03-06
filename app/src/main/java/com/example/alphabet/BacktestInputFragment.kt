package com.example.alphabet

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
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
import com.example.alphabet.databinding.BacktestStrategyRowBinding
import com.example.alphabet.databinding.FragmentBacktestInputBinding
import com.example.alphabet.databinding.InputChipBinding
import java.net.SocketTimeoutException

//TODO: Recent Backtests
class BacktestInputFragment: Fragment() {
    private val viewModel: StrategyViewModel by activityViewModels()
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private val symbolStrategyWeight = listOf(0.85f, 0.15f)
    private var _binding:FragmentBacktestInputBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBacktestInputBinding.inflate(inflater, container, false)

        binding.topAppBar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.set_time_period -> {
                    val action = BacktestInputFragmentDirections.actionBacktestInputFragmentToTimePeriodBottomSheetFragment()
                    findNavController().navigate(action)
                    true
                }
                R.id.confirm_button -> {
                    // todo: review viewModel.symbolStrategyList
                    viewModel.symbolStrategyList.clear()
                    for (symbol in viewModel.symbolList) {
                        for (strategy in viewModel.strategyList.value!!) {
                            viewModel.symbolStrategyList.add(BacktestInput(symbol, strategy))
                        }
                    }
                    val action = BacktestInputFragmentDirections.actionBacktestInputFragmentToBacktestResultFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }

//        binding.dateRangeLayout.apply {
//            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//            setContent {
//                DateRangeCard()
//            }
//        }

        viewModel.strategyList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.emptyRvText.visibility = View.VISIBLE
            } else {
                binding.emptyRvText.visibility = View.GONE
            }
        }
        viewModel.strategyList.value?.forEachIndexed { i, v ->
            val row = BacktestStrategyRowBinding.inflate(inflater, binding.strategyRv, false)
            row.strategyText.text = v.strategyName
            row.cancelButton.setOnClickListener {
                viewModel.strategyList.value = viewModel.strategyList.value?.filterIndexed { index, _ -> index != binding.strategyRv.indexOfChild(row.root) }
                binding.strategyRv.removeView(row.root)
            }
            binding.strategyRv.addView(row.root)
        }
        binding.addSymbolButton.setOnClickListener {
            addSymbol(binding.symbolText)
        }

        viewModel.symbolList.forEach { addSymbolChip(it) }
        binding.symbolText.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                addSymbol(textView)
                true
            } else {
                false
            }
        }
        binding.createStrategyButton.setOnClickListener {
            val action =
                BacktestInputFragmentDirections.actionBacktestInputFragmentToSelectStrategyFragment(-1)
            findNavController().navigate(action)
        }

        return binding.root
//        return ComposeView(requireContext()).apply {
//            setContent {
//                MaterialTheme {
//                    BacktestInputScreen()
//                }
//            }
//        }
    }

    fun addSymbolChip(symbol: String) {
        val chip = InputChipBinding.inflate(LayoutInflater.from(requireContext())).chip
        chip.text = symbol
        chip.setOnCloseIconClickListener {
            binding.symbolChipGroup.removeView(chip)
            viewModel.symbolList.remove(chip.text)
        }
        binding.symbolChipGroup.addView(chip)
    }

    fun addSymbol(textView: TextView) {
        lifecycleScope.launch {
            val symbol = textView.text.toString().uppercase()
            if (isValid(symbol)) {
                if (symbol in viewModel.symbolList) {
                    binding.symbolTextLayout.error = "You've already input this symbol"
                } else {
                    addSymbolChip(symbol)
                    viewModel.symbolList.add(symbol)
                    textView.text = ""
                    binding.symbolTextLayout.error = null
                }
            } else {
                binding.symbolTextLayout.error = "Invalid symbol"
            }
        }
    }

//    @Composable
//    fun BacktestInputScreen() {
//        Scaffold(
//            topBar = {
//                        MyTopAppBar(
//                            title = { Text("Backtest") },
//                            navigationIcon = {
//                                IconButton(onClick = { findNavController().popBackStack() }) {
//                                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
//                                }
//                            }
//                        )
//                     },
//            content = {
//                Column(
//                    Modifier
//                        .fillMaxSize()
//                        .background(grayBackground)
//                        .verticalScroll(rememberScrollState())
//                ) {
////                    Spacer(modifier = Modifier.height(10.dp))
//                    SymbolStrategyCard(viewModel.symbolStrategyList) { viewModel.addEmptyStrategy() }
//                    Spacer(modifier = Modifier.height(10.dp))
//                    DateRangeCard()
//                    Button(onClick = {
//                        processUserInput()
//                    },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(70.dp)
//                            .padding(10.dp),
//                        shape = RoundedCornerShape(50)
//                    ) {
//                        Text("BACKTEST")
//                    }
//                }
//            }
//        )
//    }


//    @Composable
//    fun SymbolStrategyCard(
//        symbolStrategyList: SnapshotStateList<BacktestInput>,
//        onAddClick: () -> Unit
//    ) {
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .animateContentSize(
//                    animationSpec = tween(easing = LinearOutSlowInEasing, durationMillis = 300)
//                ),
//            shape = RoundedCornerShape(20.dp),
//            elevation = 0.dp
//        ) {
//            Column(Modifier.padding(20.dp)) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(
//                        "Symbol and Strategy",
//                        style = MaterialTheme.typography.h6,
//                        modifier = Modifier.weight(symbolStrategyWeight[0])
//                    )
//                    IconButton(onClick = onAddClick, modifier = Modifier.weight(symbolStrategyWeight[1])) {
//                        Icon(
//                            imageVector = Icons.Default.Add,
//                            contentDescription = "Add symbol and strategy"
//                        )
//                    }
//                }
//                for (i in symbolStrategyList.indices) {
//                    SymbolStrategyRow(index = i)
//                }
//            }
//        }
//    }

//    @Composable
//    fun DateRangeCard() {
//        MyCard(modifier = Modifier
//            .fillMaxWidth(),
//        ) {
//            Column(Modifier.padding(20.dp)) {
//                Text("Date Range", style = MaterialTheme.typography.h6)
//                Spacer(modifier = Modifier.height(10.dp))
//                Row(Modifier.fillMaxWidth()) {
//                    Column(
//                        Modifier
//                            .weight(0.5f)
//                            .padding(end = 10.dp)) {
//                        DateTextField(label = "Start Date", cal = viewModel.start)
//                    }
//                    Column(
//                        Modifier
//                            .weight(0.5f)
//                            .padding(start = 10.dp)) {
//                        DateTextField(label = "End Date", cal = viewModel.end)
//                    }
//                }
//                Spacer(modifier = Modifier.height(20.dp))
//                Row {
//                    listOf(1, 2, 5, 10).forEach { year ->
//                        DefaultTimeButton(year = year)
//                        Spacer(modifier = Modifier.width(15.dp))
//                    }
//                }
//            }
//        }
//    }

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
            onClick = {
                val dpd = DatePickerDialog(
                    requireContext(),
                    R.style.MySpinnerDatePickerStyle,
                    { view, mYear, mMonth, mDay ->
                        cal.value = createCalendar(mYear, mMonth, mDay)
                    },
                    year,
                    month,
                    day
                )
                dpd.show()
            }
        )
    }



//    @Composable
//    fun SymbolStrategyRow(index: Int) {
//        val backtestInput = viewModel.symbolStrategyList[index]
//        val symbol = backtestInput.symbol
//        val strategy = backtestInput.strategyInput
//        Row(Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
//            Column(Modifier.weight(symbolStrategyWeight[0])) {
//                MyTextField(
//                    value = symbol.value,
//                    onValueChange = { symbol.value = it },
//                    leadingIcon = {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_baseline_timeline_24),
//                            contentDescription = "Symbol",
//                            tint = Color.LightGray
//                        )
//                    },
//                    trailingIcon = {
//                        if (symbol.value.isNotEmpty()) {
//                            IconButton(onClick = { symbol.value = "" }) {
//                                Icon(imageVector = Icons.Default.Clear, contentDescription = null)
//                            }
//                        }
//                    },
//                    label = { Text("Symbol") }
//                )
////                OutlinedTextField(
////                    value = symbol.value,
////                    onValueChange = { symbol.value = it },
////                    label = { Text("Symbol") },
////                    leadingIcon = {
////                        Icon(
////                            painter = painterResource(id = R.drawable.ic_baseline_timeline_24),
////                            contentDescription = "Symbol",
////                        )
////                    },
////                    modifier = Modifier.fillMaxWidth()
////                )
//                // hint text
//                Text(
//                    "e.g., TSLA, BRK-B, BTC-USD, 2800.HK",
//                    modifier = Modifier.padding(start = 5.dp),
//                    style = MaterialTheme.typography.caption,
//                    color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
//                )
//                Spacer(modifier = Modifier.height(10.dp))
//                ClickableTextField(
//                    value = if(strategy.isEmpty()) "" else strategy.strategyName,
//                    label = if(strategy.isEmpty()) null else {{Text("Strategy")}},
//                    placeholder = { Text("Select a Strategy") },
//                    leadingIcon = {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_baseline_emoji_objects_24),
//                            contentDescription = "Strategy",
//                            tint = if(strategy.isEmpty()) Color.LightGray else amber500
//                        )
//                    },
//                    trailingIcon = {
//                        if(!strategy.isEmpty()){
//                            IconButton(onClick = {
//                                viewModel.inputToSelectStrategy.value = index
//                                val action = BacktestInputFragmentDirections.actionBacktestInputFragmentToEditStrategyFragment()
//                                findNavController().navigate(action)
//                            }) {
//                                Icon(
//                                    imageVector = Icons.Default.Settings,
//                                    contentDescription = null,
//                                    tint = Color.LightGray
//                                )
//                            }
//                        } else {
//                            Icon(
//                                imageVector = Icons.Default.KeyboardArrowRight,
//                                contentDescription = null,
//                                tint = Color.LightGray
//                            )
//                        }
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    onClick = {
////                        viewModel.inputToSelectStrategy.value = index
//                        val action =
//                            BacktestInputFragmentDirections.actionBacktestInputFragmentToSelectStrategyFragment(index)
//                        findNavController().navigate(action)
//                    }
//                )
////                ClickableOutlinedTextField(
////                    value = if(strategy.isEmpty()) "" else strategy.strategyName,
////                    onValueChange = {  },
////                    label = { Text("Strategy") },
////                    placeholder = { Text("Select a Strategy") },
////                    leadingIcon = {
////                        Icon(
////                            painter = painterResource(id = R.drawable.ic_baseline_emoji_objects_24),
////                            contentDescription = "Strategy",
////                        )
////                    },
////                    modifier = Modifier
////                        .fillMaxWidth()
////                        .clickable {
////                            viewModel.inputToSelectStrategy.value = index
////                            val action =
////                                BacktestInputFragmentDirections.actionBacktestInputFragmentToSelectStrategyFragment()
////                            findNavController().navigate(action)
////                        }
////                )
//            }
//            if (viewModel.symbolStrategyList.size > 1) {
//                IconButton(
//                    onClick = {
//                        viewModel.symbolStrategyList.removeAt(index)
//                    },
//                    modifier = Modifier.weight(symbolStrategyWeight[1]),
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.ic_baseline_cancel_24),
//                        contentDescription = "Cancel",
//                        tint = Color.Gray
//                    )
//                }
//            }
//            else {
//                Spacer(modifier = Modifier.weight(symbolStrategyWeight[1]))
//            }
//        }
//    }
    

    private fun setDatePicker(editText: EditText, c: Calendar) {
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        if (editText.text.toString().isEmpty()) {
            editText.setText(sdfLong.format(c.time))
        }

        editText.setOnClickListener {
            val dpd = DatePickerDialog(requireContext(), R.style.MySpinnerDatePickerStyle, { view, mYear, mMonth, mDay ->
                val cal = createCalendar(mYear, mMonth, mDay)
                editText.setText(sdfLong.format(cal.time))
            }, year, month, day)
            dpd.show()
        }
    }

    private fun processUserInput() {
        lifecycleScope.launch {
            val symbols = viewModel.symbolStrategyList.map { it.symbol }.toSet()
            val isValid = symbols.map { it to false }.toMap().toMutableMap()
            for (symbol in symbols) {
                try {
                    withContext(Dispatchers.IO) {
                        YahooFinance.get(symbol)
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