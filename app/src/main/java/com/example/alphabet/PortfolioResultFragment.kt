package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.alphabet.MyApplication.Companion.dec
import com.example.alphabet.MyApplication.Companion.pct
import com.example.alphabet.MyApplication.Companion.sdfISO
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.database.PortfolioResultSchema
import com.example.alphabet.databinding.FragmentPortfolioResultBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.dataframe.api.*
import java.util.*

class PortfolioResultFragment: Fragment() {
    private val viewModel: StrategyViewModel by activityViewModels()
    private lateinit var databaseViewModel: DatabaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)
        val binding = FragmentPortfolioResultBinding.inflate(inflater, container, false)
        with(binding.topAppBar) {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.add_to_fav -> {
                        databaseViewModel.addPortfolioResult(viewModel.portfolioResult)
                        Toast.makeText(requireContext(), "Portfolio added to favourite", Toast.LENGTH_LONG)
                        true
                    }
                    else -> false
                }
            }
        }

        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.portfolioResultContent.visibility = View.GONE
            binding.topAppBar.visibility = View.GONE
            val df = getClosePrice(
                viewModel.symbolWeightingMap.keys.toList(),
                viewModel.start.value!!,
                viewModel.end.value!!)
            if(df != null) {
                val dates = df["date"].toList().map { (it as Calendar).time }
                val symbolWeightingMapFloat = viewModel.symbolWeightingMap.mapValues { it.value.weight.toFloat() }
                val navList = df.update { dfsOf<Float>() }
                    .perRowCol { row, col ->
                        df[col][row.index()] / df[col][0] * symbolWeightingMapFloat[col.name()]!!
                    }
                    .remove("date")
                    .add("portRet") { rowSum() } ["portRet"]
                    .toList()
                    .map { it as Float }
                val totalRet = navList.last() / navList.first() - 1
                val portRetList = (1 until navList.size).map {
                    navList[it] / navList[it-1] - 1
                }
                val sr = sharpeRatio(portRetList)
                setReturnText(requireContext(), binding.textTotalReturn, totalRet) { pct.format(it) }
                setReturnText(requireContext(), binding.textSharpeRatio, sr) { dec.format(it) }

                plotMultiLineCurve(binding.lineChart, dates, listOf(navList), listOf("Portfolio Return"), listOf(true), requireContext())
                plotPieChart(binding.pieChart, symbolWeightingMapFloat, "Portfolio Breakdown", requireContext())

                viewModel.portfolioResult = PortfolioResultSchema(
                    id = 0,
                    portfolioInputList = viewModel.symbolWeightingMap.values.toList(),
                    date = dates.map { sdfISO.format(it) },
                    nav = navList
                )
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Failed to get data")
                    .setPositiveButton("OK") { dialog, which ->
                        findNavController().popBackStack()
                    }
                    .setOnDismissListener {
                        findNavController().popBackStack()
                    }
                    .show()
            }
            binding.progressBar.visibility = View.GONE
            binding.portfolioResultContent.visibility = View.VISIBLE
            binding.topAppBar.visibility = View.VISIBLE
        }

        return binding.root
    }
}