package com.example.alphabet

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.alphabet.databinding.DialogLoadingBinding
import com.example.alphabet.util.Constants
import com.example.alphabet.viewmodel.BacktestViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.ta4j.core.BarSeriesManager
import org.ta4j.core.BaseBarSeries

class RunStrategyDialog: DialogFragment() {
    private val activityViewModel: StrategyViewModel by activityViewModels()
    private val viewModel: BacktestViewModel by navGraphViewModels(R.id.nav_graph_backtest)
    private var _dialogBinding: DialogLoadingBinding? = null
    private val dialogBinding get() = _dialogBinding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _dialogBinding = DialogLoadingBinding.inflate(layoutInflater, null, false)
        lifecycleScope.launch {
            runStrategy()
            this@RunStrategyDialog.dismiss()
        }
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.progress_running_strategy)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.cancel) { _, _ -> }
            .create()
    }

    private suspend fun runStrategy() {
        val start = activityViewModel.start.value!!
        val end = activityViewModel.end.value!!
        val strategyList = viewModel.checkedStrategy.values.toMutableList().sortedBy { it.strategy.strategyName }

        val backtestInputList = mutableListOf<BacktestInput>()
        for (stock in viewModel.stockList) {
            for (strategy in strategyList) {
                backtestInputList.add(
                    BacktestInput(stock = stock, strategyInput = strategy.strategy)
                )
            }
        }

        dialogBinding.textLoading.text = getString(R.string.progress_loading_data)
        val rawData = getMarketData(viewModel.stockList.map { it.symbol }, start, end)
        dialogBinding.textLoading.text = getString(R.string.progress_running_strategy)

        if (rawData == null) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.failed_to_download_data_error)
                .setPositiveButton("OK") { _, _, -> }
                .show()
        } else {
            val seriesMap = rawData.asIterable()
                .associate { (s, quoteList) ->
                    val series = BaseBarSeries(s)
                    quoteList.forEach { row ->
                        val date = row.date.toZonedDateTime()
                        series.addBar(date, row.open, row.high, row.low, row.close, row.volume)
                    }
                    s to series
                }

            val backtestResultList =backtestInputList
                .map { backtestInput ->
                    val series = seriesMap[backtestInput.stock.symbol]!!
                    val strategy = backtestInput.strategyInput.toStrategy(series)
                    val tradingRecord = BarSeriesManager(series).run(strategy)

                    val date =
                        List(series.barCount) { i -> Constants.sdfISO.format(series.getBar(i).endTime.toDate()) }

                    val positionList = tradingRecord.positions.map {
                        val entry = it.entry.toTradeData()
                        val exit = it.exit.toTradeData()
                        PositionData(entry = entry, exit = exit, startingType = it.entry.type)
                    }

                    val adjCloseList = rawData[backtestInput.stock.symbol]!!.map { it.adjClose.toFloat() }

                    BacktestResult(
                        backtestInput,
                        date = date,
                        adjCloseList = adjCloseList,
                        positionList = positionList
                    )
                }
            val action = BacktestStrategyInputFragmentDirections.actionGlobalBacktestResultFragment(backtestResultList = backtestResultList.toTypedArray())
            findNavController().navigate(action)
        }
    }
}