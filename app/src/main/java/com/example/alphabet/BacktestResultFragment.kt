package com.example.alphabet

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.alphabet.components.MyTopAppBar
import com.example.alphabet.databinding.FragmentBacktestResultBinding
import com.example.alphabet.ui.theme.grayBackground
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BacktestResultFragment: Fragment(R.layout.fragment_backtest_result) {
    private val viewModel: StrategyViewModel by activityViewModels()
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        _binding = FragmentBacktestResultBinding.inflate(inflater, container, false)

//        binding.optAppBar.setOnMenuItemClickListener{
//            when(it.itemId) {
//                R.id.add_to_fav -> {
//                    SaveBacktestDialog().show(requireActivity().supportFragmentManager, "SaveBacktestDialog")
//                    true
//                }
//                R.id.to_home -> {
//                    val action = BacktestResultFragmentDirections.actionGlobalHomeFragment()
//                    findNavController().navigate(action)
//                    true
//                }
//                else -> false
//            }
//        }

//        lifecycleScope.launch {
//            binding.backtestProgressBar.visibility = View.VISIBLE
//            binding.tradingResultProgressText.text =
//                resources.getText(R.string.trading_result_progress_loading_data)
//
//            with(viewModel) {
//                withContext(Dispatchers.IO) {
//                    loadData()
//                }
//                withContext(Dispatchers.Default){
//                    runStrategy(staticDataViewModel.defaultStrategy.value)
//                }
//            }
//
//
//            binding.backtestProgressBar.visibility = View.GONE
//            binding.tradingResultProgressText.visibility = View.GONE
//
//            val tabLayout = binding.tabLayout
//            val viewPagerAdapter = ViewPagerAdapter(
//                parentFragmentManager,
//                lifecycle,
//                listOf(BacktestPerfFragment(), BacktestTradesFragment(), BacktestDescFragment())
//            )
//            val viewPager2 = binding.viewPager2
//                .apply { adapter = viewPagerAdapter }
//                .apply {
//                    registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
//                        override fun onPageSelected(position: Int) {
//                            tabLayout.selectTab(tabLayout.getTabAt(position))
//                        }
//                    })
//                }
//            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabSelected(tab: TabLayout.Tab) {
//                    viewPager2.currentItem = tab.position
//                }
//
//                override fun onTabUnselected(tab: TabLayout.Tab?) {
//
//                }
//
//                override fun onTabReselected(tab: TabLayout.Tab?) {
//
//                }
//            })
//        }

        return ComposeView(requireContext()).apply {
            setContent {
                BacktestResultScreen()
            }
        }
    }

    @Composable
    fun BacktestResultPager() {
        val titles = listOf("PERF", "TRADES", "DESC")
        val viewPagerAdapter = ViewPagerAdapter(
            parentFragmentManager,
            lifecycle,
            listOf(BacktestPerfFragment(), BacktestTradesFragment(), BacktestDescFragment())
        )
        Column {
            var tabLayout: TabLayout? = null
            var viewPager2: ViewPager2? = null

            // Tab Layout
            AndroidView(
                ::TabLayout,
                modifier = Modifier.fillMaxWidth()
            ) { v ->
                v.setBackgroundColor(grayBackground.toArgb())
                v.setTabTextColors(Color.GRAY, Color.BLACK)
                titles.forEach { title ->
                    v.addTab(v.newTab().setText(title))
                }
                v.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {
                        viewPager2?.currentItem = tab.position
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {

                    }
                })
                tabLayout = v
            }

            // View Pager
            AndroidView(::ViewPager2) { v ->
                v.apply { adapter = viewPagerAdapter }
                .apply {
                    registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            tabLayout?.selectTab(tabLayout?.getTabAt(position))
                        }
                    })
                }
                viewPager2 = v
            }
        }
    }

    @Composable
    fun BacktestResultScreen() {
        Scaffold(
            topBar = {
                MyTopAppBar(
                    title = { Text("Backtest Report") },
                    navigationIcon = {
                                     IconButton(onClick = { findNavController().popBackStack() }) {
                                         Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                                     }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                SaveBacktestDialog().show(
                                    requireActivity().supportFragmentManager,
                                    "SaveBacktestDialog"
                                )
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_favorite_border_24),
                                contentDescription = ""
                            )
                        }

                        IconButton(
                            onClick = {
                                val action = BacktestResultFragmentDirections.actionBacktestResultFragmentToHomeFragment()
                                findNavController().navigate(action)
                            }
                        ) {
                            Icon(painter = painterResource(id = R.drawable.ic_outline_home_24), contentDescription = null)
                        }
                    }
                )
            },
        ) {
            var isLoading by remember { mutableStateOf(true) }

            if (isLoading) {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(10.dp))
                    Text("Loading", color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium))
                }
                LaunchedEffect(key1 = null) {
                    this.launch {
                        with(viewModel) {
                            withContext(Dispatchers.IO) {
                                loadData()
                            }
                            withContext(Dispatchers.Default){
                                runStrategy()
                            }
                        }
                        isLoading = false
                    }
                }
            } else {
                BacktestResultPager()
            }
        }
    }
}