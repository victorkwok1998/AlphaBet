package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.alphabet.MyApplication.Companion.dec
import com.example.alphabet.MyApplication.Companion.pct
import com.example.alphabet.MyApplication.Companion.sdfISO
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.database.PortfolioResultSchema
import com.example.alphabet.databinding.DialogSavePortBinding
import com.example.alphabet.databinding.FragmentPortfolioResultBinding
import com.example.alphabet.viewmodel.PortfolioViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.dataframe.api.*
import java.util.*

class PortfolioResultFragment: Fragment() {
    private lateinit var databaseViewModel: DatabaseViewModel
    private val args: PortfolioResultFragmentArgs by navArgs()
    private var _binding: FragmentPortfolioResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)
        _binding = FragmentPortfolioResultBinding.inflate(inflater, container, false)
        val portResult = args.portResult

        with(binding.topAppBar) {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            title = portResult.name
            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.add_to_fav -> {
                        val dialogBinding = DialogSavePortBinding.inflate(inflater, container, false)
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Save Portfolio")
                            .setView(dialogBinding.root)
                            .setPositiveButton("Save") { _, _ ->
                                val portName = dialogBinding.textLayoutSavePort.editText!!.text.toString()
                                if (portName.isEmpty()) {
                                    dialogBinding.textLayoutSavePort.error = "Please input portfolio name"
                                } else {
                                    args.portResult.apply {
                                        this.name = portName
                                        databaseViewModel.addPortfolioResult(this)
                                        title = portName
                                        Toast.makeText(requireContext(), getString(R.string.saved), Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                        true
                    }
                    R.id.button_edit -> {
                        val action = PortfolioResultFragmentDirections.actionPortfolioResultFragmentToNavGraphPort(portResult)
                        findNavController().navigate(action)
                        true
                    }
                    else -> false
                }
            }
        }
        generateReport(portResult)

        return binding.root
    }
    private fun generateReport(portResult: PortfolioResultSchema) {
        val navList = portResult.nav
        val dates = portResult.date.map { it.toDate() }
        val totalRet = navList.last() / navList.first() - 1
        val portRetList = navToReturn(navList)
        val sr = sharpeRatio(portRetList)
        val mdd = maxDrawDown(navList)
        setReturnText(requireContext(), binding.textTotalReturn, totalRet) { pct.format(it) }
        setReturnText(requireContext(), binding.textSharpeRatio, sr) { dec.format(it) }
        setReturnText(requireContext(), binding.textMdd, mdd) { pct.format(it) }

        plotMultiLineCurve(binding.lineChart, dates, listOf(navList), listOf("Portfolio Return"), listOf(true), requireContext())
        plotPieChart(
            context = requireContext(),
            pieChart = binding.pieChart,
            labelToVal = portResult.portfolioInputList.associate { it.stock.symbol to it.weight.toFloat() },
            label = "",
            touchEnabled = true
        )
    }
}