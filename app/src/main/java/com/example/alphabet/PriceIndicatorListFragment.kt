package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.alphabet.databinding.FragmentPriceIndicatorListBinding

class PriceIndicatorListFragment : Fragment() {
    private var _binding: FragmentPriceIndicatorListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StrategyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPriceIndicatorListBinding.inflate(inflater, container, false)

        val priceIndicatorList = resources.getStringArray(R.array.price_indicator_list)

        binding.priceIndicatorList.adapter = ArrayAdapter(
            requireContext(),
            R.layout.single_row_layout,
            priceIndicatorList
        )

        binding.priceIndicatorList.setOnItemClickListener { parent, view, position, id ->
            val indName = priceIndicatorList[position]
            if (requireArguments().getSerializable("primSec") == PrimSec.PRIMARY) {
                viewModel.primaryInd.value = IndicatorInput(IndType.INDICATOR, indName, mutableListOf())
            } else {
                viewModel.secondaryInd.value = IndicatorInput(IndType.INDICATOR, indName, mutableListOf())
            }
            findNavController().popBackStack()
        }

        return binding.root
    }
}