package com.example.alphabet

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alphabet.databinding.DialogLoadingBinding
import com.example.alphabet.util.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ta4j.core.BarSeriesManager
import org.ta4j.core.BaseBarSeries

class RunStrategyDialog: DialogFragment() {
    private val args: RunStrategyDialogArgs by navArgs()
//    private val viewModel: BacktestViewModel by navGraphViewModels(R.id.nav_graph_backtest)
    private var _dialogBinding: DialogLoadingBinding? = null
    private val dialogBinding get() = _dialogBinding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _dialogBinding = DialogLoadingBinding.inflate(layoutInflater, null, false)
        val job = lifecycleScope.launch {
            runStrategy()
            this@RunStrategyDialog.dismiss()
        }
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.loading)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.cancel) { _, _ -> job.cancel() }
            .create()
    }

    private suspend fun runStrategy() {
        val start = args.start
        val end = args.end
        val strategyList = args.strategyList.sortedBy { it.strategy.strategyName }

        val backtestInputList = mutableListOf<BacktestInput>()
        for (stock in args.stockList) {
            for (strategy in strategyList) {
                backtestInputList.add(
                    BacktestInput(
                        stock = stock,
                        strategyInput = strategy,
                        transactionCost = args.transactionCost
                    )
                )
            }
        }

        dialogBinding.textLoading.text = getString(R.string.progress_loading_data)
        val rawData = getMarketData(args.stockList.map { it.symbol }, start, end)
        dialogBinding.textLoading.text = getString(R.string.progress_running_strategy)

        if (rawData == null) {
            failToDownloadDialog(requireContext())
        } else {
            val backtestResultList = withContext(Dispatchers.Default) {
                val seriesMap = rawData.asIterable()
                    .associate { (s, quoteList) ->
                        val series = BaseBarSeries(s)
                        quoteList.forEach { row ->
                            val date = row.date.toZonedDateTime()
                            series.addBar(date, row.open, row.high, row.low, row.close, row.volume)
                        }
                        s to series
                    }
                backtestInputList
                    .map { backtestInput ->
                        val series = seriesMap[backtestInput.stock.symbol]!!
                        val strategy =
                            backtestInput.strategyInput.strategy.toStrategy(series)
                        val tradingRecord = BarSeriesManager(series).run(strategy)

                        val date =
                            List(series.barCount) { i ->
                                Constants.sdfISO.format(
                                    series.getBar(
                                        i
                                    ).endTime.toDate()
                                )
                            }

                        val adjCloseList =
                            rawData[backtestInput.stock.symbol]!!.map { it.adjClose.toFloat() }

                        val positionList = tradingRecord.positions.map {
                            val entry = it.entry.toTradeData(
                                date[it.entry.index],
                                adjCloseList[it.entry.index]
                            )
                            val exit = it.exit.toTradeData(
                                date[it.exit.index],
                                adjCloseList[it.exit.index]
                            )
                            PositionData(
                                entry = entry,
                                exit = exit,
                                startingType = it.entry.type,
                                transactionCost = backtestInput.transactionCost
                            )
                        }

                        BacktestResult(
                            backtestInput,
                            date = date,
                            adjCloseList = adjCloseList,
                            positionList = positionList
                        )
                    }
            }
                val action = if(args.toTable)
                    RunStrategyDialogDirections.actionRunStrategyDialogToMetricTableFragment(backtestResultList = backtestResultList.toTypedArray())
                else
                    RunStrategyDialogDirections.actionRunStrategyDialogToBacktestResultFragment(backtestResultList = backtestResultList.toTypedArray())
                findNavController().navigate(action)
        }
    }
}