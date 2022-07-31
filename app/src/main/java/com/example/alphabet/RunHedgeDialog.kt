package com.example.alphabet

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.alphabet.databinding.DialogLoadingBinding
import com.example.alphabet.viewmodel.HedgeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.join
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.nield.kotlinstatistics.simpleRegression

class RunHedgeDialog: DialogFragment() {
    private var _dialogBinding: DialogLoadingBinding? = null
    private val dialogBinding get() = _dialogBinding!!
    private val viewModel: HedgeViewModel by navGraphViewModels(R.id.nav_graph_hedge)
    private val args: HedgeResultFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _dialogBinding = DialogLoadingBinding.inflate(layoutInflater, null, false)
        val job = lifecycleScope.launch {
            runHedge()
            this@RunHedgeDialog.dismiss()
        }
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.progress_running_strategy)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.cancel) { _, _ -> job.cancel() }
            .create()
    }

    private suspend fun runHedge() {
        val start = args.port.date.first().toCalendar()
        val end = args.port.date.last().toCalendar()
        val dfPort = dataFrameOf(
            args.port.date.map { it.toCalendar() }.toColumn("date"),
            args.port.nav.toColumn("nav")
        )
        dialogBinding.textLoading.text = getString(R.string.progress_loading_data)
        val hedgePrice = getClosePrice(listOf(args.hedgeStock), start, end)
        if (hedgePrice != null) {
            val dfRet = hedgePrice
                .join(dfPort) { "date" match "date" }
                .run { priceToReturn(this) }

            viewModel.regressionData = dfRet[args.hedgeStock].toList()
                .map { it as Float }
                .zip(dfRet["nav"].toList().map { it as Float })
            viewModel.regression = viewModel.regressionData.simpleRegression()

            val action = RunHedgeDialogDirections.actionRunHedgeDialogToHedgeResultFragment(args.hedgeStock, args.port)
            findNavController().navigate(action)
        } else {
            failToDownloadDialog(requireContext())
        }

    }
}