package com.example.alphabet

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.alphabet.MyApplication.Companion.sdfISO
//import com.example.alphabet.database.BacktestResultCashFlowSchema
import com.example.alphabet.database.BacktestResultSchema
import com.example.alphabet.database.DatabaseViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import kotlin.collections.ArrayList

class SaveBacktestDialog: DialogFragment() {
    private lateinit var databaseViewModel: DatabaseViewModel
    private val args: SaveBacktestDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)

        val items = args.backtestResultList
        val selected = BooleanArray(items.size) {false}

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Save Backtest")
            .setMultiChoiceItems(items.map { it.backtestInput.getShortName() }.toTypedArray(), selected) { dialog,which,isChecked ->
                selected[which] = isChecked
            }
            .setPositiveButton("OK") { dialog, id ->
                selected.forEachIndexed { i, isChecked ->
                    if (isChecked) {
                        databaseViewModel.addBacktestResult(BacktestResultSchema(0, args.backtestResultList[i]))
                    }
                }
                if (selected.any())
                    Toast.makeText(requireContext(), getString(R.string.saved), Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}