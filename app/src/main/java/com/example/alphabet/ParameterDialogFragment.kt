package com.example.alphabet

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.Serializable

//private const val IND_NAME = "indName"
//private const val PARAM_NAMES = "paramNames"
private const val INDICATOR_INPUT = "indicatorInput"
private const val PRIM_SEC = "primSec"
private const val IS_POP_BACK_STACK = "isPopBackStack"

class ParameterDialogFragment() :
    DialogFragment() {
    private val viewModel: StrategyViewModel by activityViewModels()
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private val paramTextLayouts = mutableListOf<TextInputLayout>()  // save IDs of edit text for retrieving input
    private lateinit var layout: LinearLayout
    private lateinit var primSec: PrimSec
//    private lateinit var indName: String
//    private lateinit var paramNames: List<String>
    private lateinit var indicatorInput: IndicatorInput
    private var isPopBackStack = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            indicatorInput = it.getParcelable<IndicatorInput>(INDICATOR_INPUT)!!
            primSec = it.get(PRIM_SEC) as PrimSec
            isPopBackStack = it.getBoolean(IS_POP_BACK_STACK)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        layout = layoutInflater.inflate(R.layout.fragment_parameter_dialog, null, false) as LinearLayout

        val paramName = staticDataViewModel.indToParamList.value[indicatorInput.indName]

        paramName?.zip(indicatorInput.indParamList)?.forEach { (param, value) ->
            val paramRow = layoutInflater.inflate(R.layout.param_input_row_layout, layout, false)
            paramRow.findViewById<TextView>(R.id.param_name_label).text = param
            paramRow.findViewById<TextInputEditText>(R.id.param_value_text).setText(value)
            paramTextLayouts.add(paramRow.findViewById(R.id.param_value_text_layout))

            layout.addView(paramRow)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(indicatorInput.indName)
            .setView(layout)
            .setPositiveButton("Create") { _, _ -> }
            .setNegativeButton("Cancel") { _, _ -> }
            .create()
    }

    override fun onStart() {
        super.onStart()

        (dialog as AlertDialog?)?.apply {
            this.getButton(Dialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    val paramsInput = paramTextLayouts.map { paramId ->
                        paramId.editText?.text.toString()
                    }
                    if (isParamValuesValid(paramTextLayouts)) {
                        when (primSec) {
                            PrimSec.PRIMARY -> {
//                                viewModel.primaryIndParams.value = paramsInput
                                viewModel.primaryInd.value = IndicatorInput(indicatorInput.indType, indicatorInput.indName, paramsInput.toMutableList())
                            }
                            PrimSec.SECONDARY -> {
//                                viewModel.secondaryIndParams.value = paramsInput
                                viewModel.secondaryInd.value = IndicatorInput(indicatorInput.indType, indicatorInput.indName, paramsInput.toMutableList())
                            }
                        }
                        dismiss()
                        if (isPopBackStack)
                            findNavController().popBackStack()
                    }
                }
        }
    }

    private fun isParamValuesValid(textFields: List<TextInputLayout>): Boolean {
        var res = true
        textFields.forEach {
            if (it.editText?.text.toString().isEmpty()) {
                it.error = "Required"
                res = false
            } else {
                it.error = null
            }
        }
        return res
    }

    companion object {
        const val TAG = "InputParameterDialog"

        @JvmStatic
        fun newInstance(indicatorInput: IndicatorInput, primSec: Serializable?, isPopBackStack: Boolean) =
            ParameterDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(INDICATOR_INPUT, indicatorInput)
//                    putString(IND_NAME, indName)
//                    putStringArrayList(PARAM_NAMES, paramNames)
                    putSerializable(PRIM_SEC, primSec)
                    putBoolean(IS_POP_BACK_STACK, isPopBackStack)
                }
            }

        }
    }