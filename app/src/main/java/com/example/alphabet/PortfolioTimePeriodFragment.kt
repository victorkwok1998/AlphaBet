package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.navGraphViewModels
import com.example.alphabet.databinding.DialogTimePeriodBinding
import com.example.alphabet.ui.setTimePeriod
import com.example.alphabet.viewmodel.PortfolioViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PortfolioTimePeriodFragment: BottomSheetDialogFragment() {
    private val viewModel: PortfolioViewModel by navGraphViewModels(R.id.nav_graph_port)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogTimePeriodBinding.inflate(inflater, container, false)
            .setTimePeriod(viewModel.start, viewModel.end, viewLifecycleOwner)
        return binding.root
    }
}