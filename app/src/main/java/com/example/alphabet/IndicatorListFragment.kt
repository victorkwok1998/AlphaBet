package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.databinding.FragmentIndicatorListBinding
import com.example.alphabet.viewmodel.IndicatorViewModel

class IndicatorListFragment: Fragment(), IndicatorAdapter.OnItemClickListener {
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private val viewModel: IndicatorViewModel by navGraphViewModels(R.id.nav_graph_indicator)
    private var _binding: FragmentIndicatorListBinding? = null
    private val binding get() = _binding!!
    private val args: IndicatorListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIndicatorListBinding.inflate(inflater, container, false)
        viewModel.indicator = args.indicator

        with(binding.indicatorAppBar) {
            setNavigationOnClickListener { findNavController().popBackStack() }
            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.search -> {

                        true
                    }
                    else -> false
                }
            }
        }


//        val viewPager = binding.indicatorListViewPager
//        val tabLayout = binding.indicatorListTabLayout

        val indicators = staticDataViewModel.indicatorStatic
//            .filter { it.indType == IndType.INDICATOR }

        val technicalIndicators = indicators.filter { it.indCat == IndCat.TECHNICAL }
        val priceIndicators = indicators.filter { it.indCat == IndCat.PRICE }

        var otherIndicators = staticDataViewModel.indicatorStatic
            .filter { it.indCat == IndCat.OTHER }

        if (args.entryExit == EntryExit.ENTRY) {
            otherIndicators = otherIndicators.filter { !it.isExitOnly }
        }

        with(binding.rvIndicator) {
            setIndicatorList(technicalIndicators)
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        binding.chipGroupIndicatorType.setOnCheckedStateChangeListener { _, checkedIds ->
            when(checkedIds[0]) {
                R.id.chip_technical -> setIndicatorList(technicalIndicators)
                R.id.chip_price -> setIndicatorList(priceIndicators)
                R.id.chip_other -> setIndicatorList(otherIndicators)
                R.id.chip_constant -> setIndicatorList(indicators.filter { it.indCat == IndCat.CONSTANT })
            }
        }
        // view pager
//        viewPager.adapter = ViewPagerAdapter(
//            childFragmentManager,
//            lifecycle,
//            listOf(
//                TechnicalIndicatorListFragment.newInstance(indicators),
//                PriceIndicatorListFragment(),
//                    //.apply { arguments = Bundle().apply { putSerializable("primSec", args.primSec) } },
//                TechnicalIndicatorListFragment.newInstance(otherIndicators)
//            )
//        )
//
//        val titles = resources.getStringArray(R.array.indicator_list_tab_titles)
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = titles[position]
//        }.attach()

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

    override fun onItemClick(selectedItem: IndicatorStatic) {
        if (selectedItem.paramName.isEmpty()) {
            with(viewModel.indicator) {
                indName = selectedItem.indName
                indType = selectedItem.indType
                indParamList = MutableList(selectedItem.paramName.size) { "" }
            }
            findNavController().popBackStack()
        } else {
            val action =
                IndicatorListFragmentDirections.actionGlobalParameterDialogFragment(
                    viewModel.indicator,
                    IndicatorInput(
                        selectedItem.indType,
                        selectedItem.indName,
                        MutableList(selectedItem.paramName.size) { "" })
                )
            findNavController().navigate(action)
        }
    }

    private fun setIndicatorList(indicators: List<IndicatorStatic>) {
        val adapter = IndicatorAdapter(indicators, this, requireContext())
        binding.rvIndicator.adapter = adapter
    }
}