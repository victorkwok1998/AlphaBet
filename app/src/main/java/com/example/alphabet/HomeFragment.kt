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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.alphabet.components.MyCard
import com.example.alphabet.components.MyTopAppBar
import com.example.alphabet.components.SearchBar
import com.example.alphabet.components.StrategyList
import com.example.alphabet.databinding.FragmentHomeBinding
import com.example.alphabet.databinding.ModalBottomSheetContentBinding
import com.example.alphabet.ui.theme.grayBackground
import com.example.alphabet.ui.theme.green500
import com.example.alphabet.ui.theme.red500
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import kotlinx.serialization.encodeToString

// /data/user/0/com.example.alphabet/files/

class HomeFragment: Fragment()  {
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private val viewModel: StrategyViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val nestedNavController = (childFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController

        lifecycleScope.launch {
            if (staticDataViewModel.indToParamList.value.isEmpty()) {
                // Read static data
                withContext(Dispatchers.IO) {
                    staticDataViewModel.indicatorStatic.value =
                        Json.decodeFromString<List<IndicatorStatic>>(getJsonDataFromAsset(requireContext(), "indicatorStatic.json")).sortedBy { it.indName }
                    staticDataViewModel.indToParamList.value = staticDataViewModel.indicatorStatic.value.map{it.indName to it.paramName}.toMap()

                    staticDataViewModel.radarChartRange.value =
                        Json.decodeFromString(getJsonDataFromAsset(requireContext(), "radarChartRange.json"))
                }
            }
        }

//        binding.bottomNavigation.setupWithNavController(nestedNavHostFragment.navController)
        binding.bottomNavigation.setOnItemSelectedListener {
            if(it.itemId == R.id.create_dialog) {
                BottomSheetDialog(requireContext()).apply {
                    val modalSheetBinding = ModalBottomSheetContentBinding.inflate(inflater, container, false)
                    val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    modalSheetBinding.createBacktest.setOnClickListener {
                        this.dismiss()
                        viewModel.reset()
                        val action = HomeFragmentDirections.actionHomeFragmentToBacktestInputFragment()
                        navController.navigate(action)

                    }
                    modalSheetBinding.createStrategy.setOnClickListener {
                        this.dismiss()
                        viewModel.resetCustomStrategy()
                        val action = HomeFragmentDirections.actionHomeFragmentToCreateStrategyFragment(isEdit = false)
                        navController.navigate(action)
                    }
                    modalSheetBinding.createPortfolio.setOnClickListener {
                        this.dismiss()
                        viewModel.symbolWeightingMap.clear()
                        val action = HomeFragmentDirections.actionHomeFragmentToPortfolioFragment()
                        navController.navigate(action)
                    }
                    this.setContentView(modalSheetBinding.root)
                    this.show()
                }
                false
            }
            else{
                nestedNavController.navigate(it.itemId)
                true
            }
        }

        return binding.root
//        return ComposeView(requireContext()).apply {
//            setContent {
//                MaterialTheme() {
//                    HomeScreen()
//                }
//            }
//        }
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
                            Json.decodeFromString<Map<String, List<String>>>(getJsonDataFromAsset(requireContext(), "indicatorStatic.json"))
                        staticDataViewModel.defaultStrategy.value =
                            Json.decodeFromString<List<StrategyInput>>(getJsonDataFromAsset(requireContext(), "defaultStrategy.json"))
                                .sortedBy { it.strategyName }
                                .toMutableList()

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
        val selectedItem = viewModel.selectedItem.value
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
                    MyTopAppBar(
                        title = {
                            SearchBar(
                                value = searchText,
                                onValueChange = { searchText = it },
                            )
                        },
//                        navigationIcon = {
//                            IconButton(onClick = { /*TODO*/ }) {
//                                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
//                            }
//                        }
                    )
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
            floatingActionButton = {
                if (selectedItem == 1) {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "Custom") },
                        icon = { Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null
                        ) },
                        onClick = {
                            viewModel.resetCustomStrategy()
                            val action =  HomeFragmentDirections.actionHomeFragmentToCreateStrategyFragment(false)
                            findNavController().navigate(action)
                        })
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
                                        viewModel.selectedItem.value = index
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
            MyCard(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            {
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())) {
                    StrategyList(
                        strategies = strategies,
                        onOptionSelected = {
                            viewModel.customStrategy.value = it.toCustomStrategyInput()
                            val action = HomeFragmentDirections.actionHomeFragmentToCreateStrategyFragment(true)
                            findNavController().navigate(action)
                        }
                    )
                    // Extra space for scroll above fab
                    Spacer(modifier = Modifier.height(60.dp))
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                ) {
                    Text("Create a Backtest", style = MaterialTheme.typography.subtitle1)
                }
            }
        }
    }
}