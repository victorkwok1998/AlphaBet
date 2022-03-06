package com.example.alphabet

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import com.example.alphabet.MyApplication.Companion.sdfISO
//import com.example.alphabet.database.BacktestResultCashFlowSchema
import com.example.alphabet.database.BacktestResultSchema
import com.example.alphabet.database.DatabaseViewModel
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.serialization.encodeToString

class SaveBacktestDialog: DialogFragment() {
    private val viewModel: StrategyViewModel by activityViewModels()
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private lateinit var databaseViewModel: DatabaseViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)

        return requireActivity().let {
            val selectedItems = ArrayList<Int>()
            val builder = AlertDialog.Builder(it)

            val items = viewModel.symbolStrategyStringList().toTypedArray()

            builder.setTitle("Add to Favourite")
                .setMultiChoiceItems(
                    items,
                    null,
                    DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            selectedItems.add(which)
                        } else if (selectedItems.contains(which)) {
                            // Else, if the item is already in the array, remove it
                            selectedItems.remove(which)
                        }
                    }
                )
                .setPositiveButton("OK"
                ) { dialog, id ->
                    selectedItems.forEach { i ->
//                        val strategyId = viewModel.symbolStrategyList[i].second.value
                        val strategyInput = viewModel.symbolStrategyList[i].strategyInput
                        val symbol = viewModel.symbolStrategyList[i].symbol
                        val series = viewModel.seriesMap[symbol]!!

                        val date = List(series.barCount) { j ->
                            Date.from(series.getBar(j).endTime.toInstant())
                                .run { sdfISO.format(this) }
                        }
                        val tradingRecord = viewModel.metrics.value[i].tradingRecord
                        val cashFlow = getCashFlow(series, tradingRecord)

                        databaseViewModel.addBacktestResult(BacktestResultSchema(0, BacktestResult(symbol, strategyInput, date, cashFlow)))

                        staticDataViewModel.myBacktestResults.add(
                            BacktestResult(symbol, strategyInput, date, cashFlow)
                        )
                    }

                    staticDataViewModel.myBacktestResults
                        .toList()
                        .apply {
                            File(requireContext().filesDir, "myBacktestResults.json")
                                .writeText(Json.encodeToString(this))
                        }
                    Toast.makeText(context, getString(R.string.set_fav), Toast.LENGTH_LONG).show()
                }
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        // do nothing
                    })
            builder.create()
        }
    }
}