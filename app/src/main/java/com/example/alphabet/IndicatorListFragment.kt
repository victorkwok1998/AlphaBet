package com.example.alphabet

import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
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
        }

        val indicators = staticDataViewModel.indicatorStatic
            .filterNot {
                ((args.entryExit == EntryExit.ENTRY) && (it.indType == IndType.EXIT_RULE))
            }.filterNot {
                ((args.entryExit == EntryExit.EXIT) && (it.indType == IndType.ENTRY_RULE))
            }
        with(binding.rvIndicatorSearch) {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            adapter = IndicatorAdapter(indicators, this@IndicatorListFragment, requireContext())
        }
        binding.searchViewIndicatorSearch.editText.doAfterTextChanged { et ->
            val queryResult = indicators.filter {
                it.indName.contains(et.toString(), ignoreCase = true)
            }
            binding.rvIndicatorSearch.adapter = IndicatorAdapter(queryResult, this@IndicatorListFragment, requireContext())
        }

        val technicalIndicators = indicators.filter { it.indType == IndType.TECHNICAL }
        val priceIndicators = indicators.filter { it.indType == IndType.PRICE }

        val otherIndicators = when (args.entryExit) {
            EntryExit.ENTRY -> staticDataViewModel.indicatorStatic
                .filter { it.indType == IndType.ENTRY_RULE }
            EntryExit.EXIT -> staticDataViewModel.indicatorStatic
                .filter { it.indType == IndType.EXIT_RULE }
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
                R.id.chip_constant -> setIndicatorList(indicators.filter { it.indType == IndType.CONSTANT })
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

    private fun search(newText: String?): Boolean {
        if (newText != null) {
            val indicators = staticDataViewModel.indicatorStatic.filter {
                it.indName.contains(newText, ignoreCase = true)
            }
            setIndicatorList(indicators)
        }
        return true
    }


}