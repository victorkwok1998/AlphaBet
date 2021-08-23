package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.example.alphabet.databinding.FragmentEquityCurveBinding

class EquityCurveFragment: Fragment(R.layout.fragment_equity_curve) {
    private var _binding: FragmentEquityCurveBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StrategyViewModel by navGraphViewModels(R.id.strategy_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEquityCurveBinding.inflate(inflater, container, false)

        return binding.root
    }
}