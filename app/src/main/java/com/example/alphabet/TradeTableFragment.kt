package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alphabet.databinding.FragmentTradeTableBinding
import com.example.alphabet.ui.setChipGroup
import com.example.alphabet.util.Constants
import de.codecrafters.tableview.model.TableColumnWeightModel
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter
import java.util.concurrent.TimeUnit

class TradeTableFragment: Fragment() {
    private val args: TradeTableFragmentArgs by navArgs()
    private var _binding: FragmentTradeTableBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTradeTableBinding.inflate(inflater, container, false)
        binding.topAppBar.setNavigationOnClickListener { findNavController().popBackStack() }

        plotBacktestResultTrades(args.backtestResultList[args.checkedIndex])
        setTradeTable(args.backtestResultList[args.checkedIndex].positionList)
        setChipGroup(requireContext(), args.backtestResultList, binding.chipGroupTradesTradetable, args.checkedIndex) {
            val i = it.indexOf(true)
            plotBacktestResultTrades(args.backtestResultList[i])
            setTradeTable(args.backtestResultList[i].positionList)
        }
        binding.chipGroupTradesTradetable.post {
            val chip = binding.chipGroupTradesTradetable.getChildAt(args.checkedIndex)
            binding.horizontalScrollViewTradetable.scrollTo(chip.left - chip.paddingLeft, chip.top)
        }

        return binding.root
    }

    private fun plotBacktestResultTrades(backtestResult: BacktestResult) {
        plotBacktestResultTrades(requireContext(), binding.chartTradesTradetable, backtestResult)
    }

    private fun setTradeTable(positionList: List<PositionData>) {
        val data = positionList.map {
            arrayOf(
                it.entry.date,
                it.exit.date,
                TimeUnit.DAYS.convert(it.exit.date.toDate().time - it.entry.date.toDate().time, TimeUnit.MILLISECONDS).toString(),
                Constants.pct.format(it.getPnlPct())
            )
        }
        val dAdapter = SimpleTableDataAdapter(requireContext(), data).apply {
            setTextSize(14)
            setTextColor(context.getTextColorPrimary())
            setPaddingTop(25)
            setPaddingBottom(25)
        }
        val columnModel = TableColumnWeightModel(data[0].size).apply {
            setColumnWeight(0, 30)
            setColumnWeight(1, 30)
            setColumnWeight(2, 20)
            setColumnWeight(3, 20)
        }
        binding.tableTrades.apply {
            setDataAdapter(dAdapter)
            headerAdapter = createHeaderAdapter(requireContext(), *resources.getStringArray(R.array.trade_table_header))
            setColumnModel(columnModel)
        }
    }
}