package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.databinding.FragmentTechnicalIndicatorListBinding

private const val PRIMSEC = "primSec"
private const val INDICATORS = "indicators"

class TechnicalIndicatorListFragment: Fragment(), IndicatorAdapter.OnItemClickListener {
    private val viewModel: StrategyViewModel by activityViewModels()
//    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private var _binding: FragmentTechnicalIndicatorListBinding? = null
    private val binding get() = _binding!!
    private lateinit var indicators: List<IndicatorStatic>
    private lateinit var primSec: PrimSec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            primSec = it.get(PRIMSEC) as PrimSec
            indicators = it.getParcelableArrayList("indicators")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTechnicalIndicatorListBinding.inflate(inflater, container, false)

        val recyclerView = binding.indicatorRecyclerView
        recyclerView.adapter = IndicatorAdapter(indicators, this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        return binding.root
    }

    override fun onItemClick(position: Int) {
        val selectedItem = indicators[position]
        if (selectedItem.paramName.isEmpty()) {
            val indicator = IndicatorInput(selectedItem.indType, selectedItem.indName, mutableListOf())
            when (primSec) {
                PrimSec.PRIMARY -> {viewModel.primaryInd.value = indicator}
                PrimSec.SECONDARY -> {viewModel.secondaryInd.value = indicator}
            }
            findNavController().popBackStack()
        } else {
            parameterDialog(
                IndicatorInput(selectedItem.indType, selectedItem.indName, MutableList(selectedItem.paramName.size){""}),
                primSec,
                childFragmentManager
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(primSec: PrimSec, indicators: ArrayList<IndicatorStatic>) =
            TechnicalIndicatorListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PRIMSEC, primSec)
                    putParcelableArrayList(INDICATORS, indicators)
                }
            }
    }
}