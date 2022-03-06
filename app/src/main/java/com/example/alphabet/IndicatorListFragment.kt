package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alphabet.components.IndicatorList
import com.example.alphabet.components.MyTopAppBar
import com.example.alphabet.databinding.FragmentIndicatorListBinding
import com.example.alphabet.ui.theme.grayBackground
import com.google.android.material.tabs.TabLayoutMediator

class IndicatorListFragment: Fragment() {
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private val viewModel: StrategyViewModel by activityViewModels()
    private var _binding: FragmentIndicatorListBinding? = null
    private val binding get() = _binding!!
    private val args: IndicatorListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIndicatorListBinding.inflate(inflater, container, false)

        binding.indicatorAppBar.setNavigationOnClickListener { findNavController().popBackStack() }

        val viewPager = binding.indicatorListViewPager
        val tabLayout = binding.indicatorListTabLayout

        val indicators = staticDataViewModel.indicatorStatic.value
            .filter { it.indType == IndType.INDICATOR }
            .run { ArrayList(this) }

        val otherIndicators = staticDataViewModel.indicatorStatic.value
            .filter { it.indType == IndType.OTHER }
            .run { ArrayList(this) }
        // view pager
        viewPager.adapter = ViewPagerAdapter(
            parentFragmentManager,
            lifecycle,
            listOf(
                TechnicalIndicatorListFragment.newInstance(args.primSec, indicators),
                PriceIndicatorListFragment().apply { arguments = Bundle().apply { putSerializable("primSec", args.primSec) } },
                TechnicalIndicatorListFragment.newInstance(args.primSec, otherIndicators)
            )
        )

        val titles = resources.getStringArray(R.array.indicator_list_tab_titles)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        return binding.root
//        return ComposeView(requireContext()).apply {
//            val indicators = staticDataViewModel.indToParamList.value.keys.toList()
//            setContent {
//                MaterialTheme {
//                    IndicatorListScreen(
//                        indicators = indicators,
//                        onClick = {
//                            when(viewModel.selectedInd.value) {
//                                SelectedInd.PRIMARY -> {viewModel.primaryIndName.value = indicators[it]}
//                                SelectedInd.SECONDARY -> {viewModel.secondaryIndName.value = indicators[it]}
//                            }
////                            with(viewModel.selectedIndicator.value) {
////                                indName = indicators[it]
////                                val n = staticDataViewModel.indToParamList.value[indName]?.size ?: 0
////                                indParamList = MutableList(n) { "" }
////                            }
//                            findNavController().popBackStack()
//                        }
//                    )
//                }
//            }
//        }
    }

//    override fun onItemClick(position: Int) {
//        val indicators = staticDataViewModel.indToParamList.value.toList().sortedBy { it.first }
//        val selectedItem = indicators[position]
//
//        val args = Bundle().apply {
//            putString("indName", selectedItem.first)
//            putStringArrayList("paramNames", ArrayList(selectedItem.second))
//            putSerializable("primSec", args.primSec)
//        }
//        ParameterDialogFragment()
//            .apply { arguments = args }
//            .show(
//                childFragmentManager,
//                ParameterDialogFragment.TAG
//            )
//    }

    @Composable
    fun IndicatorListScreen(indicators: List<String>, onClick: (Int) -> Unit) {
        Scaffold(
            topBar = { MyTopAppBar(
                title = { Text("Indicators") },
                navigationIcon = {
                    IconButton(onClick = { findNavController().popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            ) },
        ) {
            Column(
                Modifier
                    .background(grayBackground)
                    .fillMaxSize()
            ) {
                IndicatorList(indicators = indicators, onClick = onClick)
            }
        }
    }
    
    @Preview
    @Composable
    fun PreviewIndicatorListScreen() {
        IndicatorListScreen(indicators = listOf("RSI", "EMA", "SMA"), onClick = {})
    }
}