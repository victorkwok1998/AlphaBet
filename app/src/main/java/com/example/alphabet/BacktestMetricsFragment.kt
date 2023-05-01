package com.example.alphabet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alphabet.databinding.FragmentBacktestMetricsBinding
import com.example.alphabet.util.Constants
import de.codecrafters.tableview.TableDataAdapter
import de.codecrafters.tableview.model.TableColumnWeightModel

class BacktestMetricsFragment: Fragment() {
    private val args: BacktestMetricsFragmentArgs by navArgs()
    private var _binding: FragmentBacktestMetricsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBacktestMetricsBinding.inflate(inflater, container, false)
        binding.topAppBar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.chipReturn.setOnCheckedChangeListener { compoundButton, b -> setUpReturn() }
        binding.chipWinRate.setOnCheckedChangeListener { compoundButton, b -> setUpWinRate() }
        binding.chipMdd.setOnCheckedChangeListener { compoundButton, b -> setUpMdd() }

        val columnModel = TableColumnWeightModel(3).apply {
            setColumnWeight(0, 20)
            setColumnWeight(1, 50)
            setColumnWeight(2, 30)
        }
        setUpReturn()
        binding.tableBacktestMetrics.apply {
            setColumnComparator(2, Comparator<MetricRow> { p0, p1 -> p0.value.compareTo(p1.value) })
            setColumnModel(columnModel)
        }
        return binding.root
    }

    private fun setUpReturn() {
        val data = args.backtestResultList.map {
            MetricRow(
                it.backtestInput.stock.symbol,
                it.backtestInput.strategyInput.strategy.strategyName,
                it.getMetrics().pnlPct
            )
        }
        binding.tableBacktestMetrics.apply {
            headerAdapter = createHeaderAdapter(requireContext(), *resources.getStringArray(R.array.metrics_table_header), resources.getString(R.string.return_rate))
            setDataAdapter(MetricDataAdapter(requireContext(), data))
        }
    }

    private fun setUpWinRate() {
        val data = args.backtestResultList.map {
            MetricRow(
                it.backtestInput.stock.symbol,
                it.backtestInput.strategyInput.strategy.strategyName,
                it.getMetrics().winRate
            )
        }
        binding.tableBacktestMetrics.apply {
            headerAdapter = createHeaderAdapter(requireContext(), *resources.getStringArray(R.array.metrics_table_header), resources.getString(R.string.win_rate))
            setDataAdapter(MetricDataAdapter(requireContext(), data))
        }
    }

    private fun setUpMdd() {
        val data = args.backtestResultList.map {
            MetricRow(
                it.backtestInput.stock.symbol,
                it.backtestInput.strategyInput.strategy.strategyName,
                it.getMetrics().mdd
            )
        }
        binding.tableBacktestMetrics.apply {
            headerAdapter = createHeaderAdapter(requireContext(), *resources.getStringArray(R.array.metrics_table_header), resources.getString(R.string.mdd))
            setDataAdapter(MetricDataAdapter(requireContext(), data))
        }
    }
}

data class MetricRow(val symbol: String, val strategyName: String, val value: Float)

class MetricDataAdapter(context: Context, data: List<MetricRow>): TableDataAdapter<MetricRow>(context, data) {
    override fun getCellView(rowIndex: Int, columnIndex: Int, parentView: ViewGroup?): View {
        val row = getRowData(rowIndex)
        return when(columnIndex) {
            0 -> tableTextView(row.symbol)
            1 -> tableTextView(row.strategyName)
            2 -> tableTextView(Constants.pct.format(row.value))
            else -> TextView(context)
        }
    }
    private fun tableTextView(s: String) = TextView(context).apply {
        text = s
        setPadding(context.dpToPx(8))
        textSize = 14f
    }
}