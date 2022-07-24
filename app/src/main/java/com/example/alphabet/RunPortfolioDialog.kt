package com.example.alphabet

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.database.PortfolioResultSchema
import com.example.alphabet.databinding.DialogLoadingBinding
import com.example.alphabet.viewmodel.PortfolioViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.dataframe.api.*
import java.util.*

class RunPortfolioDialog: DialogFragment() {
    private var _dialogBinding: DialogLoadingBinding? = null
    private val dialogBinding get() = _dialogBinding!!
    private val portfolioViewModel: PortfolioViewModel by navGraphViewModels(R.id.nav_graph_port)
    private val viewModel: StrategyViewModel by activityViewModels()
    private lateinit var databaseViewModel: DatabaseViewModel
    private val args: PortfolioInputFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _dialogBinding = DialogLoadingBinding.inflate(layoutInflater, null, false)
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)

        val job = lifecycleScope.launch {
            runPortfolio()
            this@RunPortfolioDialog.dismiss()
        }
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.progress_running_strategy)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.cancel) { _, _ -> job.cancel() }
            .create()
    }

    private suspend fun runPortfolio() {
        dialogBinding.textLoading.text = getString(R.string.progress_loading_data)
        val df = getClosePrice(
            portfolioViewModel.symbolWeightingMap.keys.toList(),
            viewModel.start.value!!,
            viewModel.end.value!!)
        dialogBinding.textLoading.text = getString(R.string.progress_running_strategy)
        if(df != null) {
            val dates = df["date"].toList().map { (it as Calendar).time }
            val symbolWeightingMapFloat = portfolioViewModel.symbolWeightingMap.mapValues { it.value.weight.toFloat() }
            val navList = df.update { dfsOf<Float>() }
                .perRowCol { row, col ->
                    df[col][row.index()] / df[col][0] * symbolWeightingMapFloat[col.name()]!!
                }
                .remove("date")
                .add("portRet") { rowSum() } ["portRet"]
                .toList()
                .map { it as Float }

            val portfolioInputList = portfolioViewModel.symbolWeightingMap.values.toList()
            val date = dates.map { MyApplication.sdfISO.format(it) }

            val portResult = args.portResult?.run {
                val newPortResult = PortfolioResultSchema(
                    this.id, this.name, portfolioInputList, date, navList
                )
                databaseViewModel.updatePortfolioResult(newPortResult)
                newPortResult
            } ?: PortfolioResultSchema(
                id = 0,
                name = "",
                portfolioInputList = portfolioViewModel.symbolWeightingMap.values.toList(),
                date = dates.map { MyApplication.sdfISO.format(it) },
                nav = navList
            )

            val action = PortfolioInputFragmentDirections.actionGlobalPortfolioResultFragment(portResult)
            findNavController().navigate(action)

        } else {
            failToDownloadDialog(requireContext())
        }
    }
}