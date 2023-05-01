package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alphabet.databinding.FragmentMetricTableBinding
import com.example.alphabet.tableview.TableViewAdapter
import com.example.alphabet.tableview.TableViewModel
import com.example.alphabet.tableview.model.CellFormat

class MetricTableFragment: Fragment() {
    private var _binding: FragmentMetricTableBinding? = null
    private val binding get() = _binding!!
    private val args: MetricTableFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMetricTableBinding.inflate(inflater, container, false)
        binding.appBarMetricTable.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        val metricsList = args.backtestResultList.map { it.getMetrics() }

        val vm = TableViewModel(
            columnNames = listOf(getString(R.string.pnl), getString(R.string.pnl_pct), getString(R.string.vol), getString(R.string.mdd), getString(R.string.win_rate)),
            rowNames = (1..metricsList.size).map { it.toString() },
            data = metricsList.map { m -> listOf(m.pnl, m.pnlPct, m.vol, m.mdd, m.winRate) },
            cellFormatList = listOf(CellFormat.FLOAT, CellFormat.PCT, CellFormat.PCT, CellFormat.PCT, CellFormat.PCT)
        )
        val adapter = TableViewAdapter(vm)
        binding.tableMetrics.setAdapter(adapter)
        adapter.setAllItemsFromVM()
        return binding.root
    }
}