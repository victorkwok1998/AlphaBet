package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alphabet.databinding.ChipLegendBinding
import com.example.alphabet.databinding.FragmentBacktestResultBinding
import com.example.alphabet.databinding.TableRowBinding
import com.example.alphabet.ui.checkedIndex
import com.example.alphabet.ui.setChipGroup
import com.example.alphabet.util.Constants.Companion.pct
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class BacktestResultFragment: Fragment() {
    private var _binding: FragmentBacktestResultBinding? = null
    private val binding get() = _binding!!
    private val args: BacktestResultFragmentArgs by navArgs()
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBacktestResultBinding.inflate(inflater, container, false)

        binding.appBarBacktestResult.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
//        binding.appBarBacktestResult.setOnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.add_to_fav -> {
//                    val action = BacktestResultFragmentDirections.actionBacktestResultFragmentToSaveBacktestDialog(args.backtestResultList)
//                    findNavController().navigate(action)
//                    true
//                }
//                else -> false
//            }
//        }

        plotEquityCurve(List(args.backtestResultList.size) {true})
        setStrategyChipGroup(binding.chipGroupEquityCurve) { plotEquityCurve(it) }

        plotSummary(List(args.backtestResultList.size) { true })
        setStrategyChipGroup(binding.chipGroupSummary) { plotSummary(it) }


        plotBacktestResultTrades(args.backtestResultList[0])
        setStrategyChipGroup(binding.chipGroupTrades, 0) {
            val i = it.indexOf(true)
            plotBacktestResultTrades(args.backtestResultList[i])
        }

        binding.buttonShareBacktest.setOnClickListener {  }
        binding.buttonSaveBacktest.setOnClickListener {
            val action = BacktestResultFragmentDirections.actionBacktestResultFragmentToSaveBacktestDialog(args.backtestResultList)
            findNavController().navigate(action)
        }
        binding.buttonViewTrades.setOnClickListener {
            val action = BacktestResultFragmentDirections.actionBacktestResultFragmentToTradeTableFragment(args.backtestResultList, binding.chipGroupTrades.checkedIndex())
            findNavController().navigate(action)
        }

        return binding.root
    }

    private fun plotEquityCurve(enabledLines: List<Boolean>) {
        plotMultiLineCurve(
            context = requireContext(),
            chart = binding.lineChartEquityCurve,
            xVals = args.backtestResultList.first().date.map { it.toDate() },
            yVals = args.backtestResultList.map { it.getCashFlow() },
            labels = args.backtestResultList.map { it.backtestInput.getShortName() },
            enabledLines = enabledLines
        )
    }

    private fun plotSummary(enabledLines: List<Boolean>) {
        val criterion = resources.getStringArray(R.array.radar_label).toList()
        val scores = args.backtestResultList.map { it.getMetrics() }
            .map { listOf(it.pnlPct, it.mdd, it.nTrade.toFloat(), it.profitFactor, it.winRate) }
            .map { metrics ->
                List(criterion.size) { i ->
                    val k = criterion[i]
                    val v = metrics[i]
                    val score =
                        staticDataViewModel.radarChartRange[k]!!.binarySearch(v)
                    when {
                        score < 0 -> -score - 1
                        else -> score
                    }.toFloat()
                }
            }

        plotRadarChart(binding.radarChartBacktest,
            labels = criterion,
            enabledLines = enabledLines,
            scoresList = scores,
            context = requireContext()
        )
    }

    private fun setStrategyChipGroup(chipGroup: ChipGroup, checkedIndex: Int = -1, onCheck: (List<Boolean>) -> Unit) {
        setChipGroup(requireContext(), args.backtestResultList, chipGroup, checkedIndex, onCheck)
    }

    private fun plotBacktestResultTrades(backtestResult: BacktestResult) {
        plotBacktestResultTrades(requireContext(), binding.chartTrades, backtestResult)
    }
}
