package com.example.alphabet

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.alphabet.databinding.FragmentParameterDialogBinding
import com.example.alphabet.databinding.ParamInputRowLayoutBinding
import com.example.alphabet.viewmodel.IndicatorViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

class ParameterDialogFragment :
    DialogFragment() {
    private val viewModel: IndicatorViewModel by navGraphViewModels(R.id.nav_graph_indicator)
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private val paramTextLayouts =
        mutableListOf<TextInputLayout>()  // save IDs of edit text for retrieving input
    private val args: ParameterDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = FragmentParameterDialogBinding.inflate(layoutInflater, null, false)

        val oldIndicator = args.oldIndicator
        val paramName = staticDataViewModel.indToParamList[oldIndicator.indName]
        val layout = binding.paramLayout

        paramName?.zip(oldIndicator.indParamList)?.forEach { (param, value) ->
            val rowBinding = ParamInputRowLayoutBinding.inflate(layoutInflater, layout, false)
//            rowBinding.paramNameLabel.text = param
            rowBinding.paramValueText.setText(value)
            rowBinding.paramValueTextLayout.hint = param
            paramTextLayouts.add(rowBinding.paramValueTextLayout)

            layout.addView(rowBinding.root)
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(oldIndicator.indName)
            .setView(binding.root)
            .setPositiveButton("OK", null)
            .setNegativeButton("Cancel") { _, _ -> dismiss() }
            .setCancelable(false)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    val paramsInput = paramTextLayouts.map { paramId ->
                        paramId.editText?.text.toString()
                    }
                    if (isParamValuesValid(paramTextLayouts)) {
                        args.newIndicator.indName = oldIndicator.indName
                        args.newIndicator.indType = oldIndicator.indType
                        args.newIndicator.indParamList = paramsInput.toMutableList()
                        findNavController().popBackStack(R.id.editRuleFragment, false)
                    }
                }
        }
        return dialog
    }

    private fun isParamValuesValid(textFields: List<TextInputLayout>): Boolean {
        var res = true
        textFields.forEach {
            if (it.editText?.text.toString().isEmpty()) {
                it.error = "Required"
                it.requestFocus()
                res = false
            } else {
                it.error = null
            }
        }
        return res
    }

}