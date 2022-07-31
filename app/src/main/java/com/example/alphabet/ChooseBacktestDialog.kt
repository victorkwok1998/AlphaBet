package com.example.alphabet

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ChooseBacktestDialog: DialogFragment() {
    private val args: ChooseBacktestDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val items = args.backtestResultList
        var checked = -1

        if (args.backtestResultList.size == 1) {
            val action = ChooseBacktestDialogDirections.actionChooseBacktestDialogToShareBacktestFragment(args.backtestResultList.first())
            findNavController().navigate(action)
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Choose Backtest")
            .setSingleChoiceItems(items.map { it.backtestInput.getShortName() }.toTypedArray(), checked) { _, which ->
                checked = which
            }
            .setPositiveButton("OK") { _, _ -> }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    if (checked >= 0) {
                        val action = ChooseBacktestDialogDirections.actionChooseBacktestDialogToShareBacktestFragment(args.backtestResultList[checked])
                        findNavController().navigate(action)
                    } else {
                        Toast.makeText(requireContext(), R.string.no_backtest_selected, Toast.LENGTH_LONG).show()
                    }
                }
        }

        return dialog
    }
}