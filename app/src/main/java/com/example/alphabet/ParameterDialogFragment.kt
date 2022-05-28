package com.example.alphabet

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.alphabet.databinding.FragmentParameterDialogBinding
import com.example.alphabet.databinding.ParamInputRowLayoutBinding
import com.example.alphabet.viewmodel.IndicatorViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ParameterDialogFragment() :
    Fragment() {
    private val viewModel: IndicatorViewModel by navGraphViewModels(R.id.nav_graph_indicator)
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private val paramTextLayouts = mutableListOf<TextInputLayout>()  // save IDs of edit text for retrieving input
    private val args: ParameterDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentParameterDialogBinding.inflate(inflater, container, false)
        val oldIndicator = args.oldIndicator
        with (binding.paramAppBar) {
            title = oldIndicator.indName
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }

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
        binding.buttonConfirmParam.setOnClickListener {
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

        return binding.root
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val layout = layoutInflater.inflate(R.layout.fragment_parameter_dialog, null, false) as LinearLayout
//        val indicatorInput = args.indicator
//
//        val paramName = staticDataViewModel.indToParamList.value[indicatorInput.indName]
//
//        paramName?.zip(indicatorInput.indParamList)?.forEach { (param, value) ->
//            val rowBinding = ParamInputRowLayoutBinding.inflate(layoutInflater, layout, false)
//            rowBinding.paramNameLabel.text = param
//            rowBinding.paramValueText.setText(value)
//            paramTextLayouts.add(rowBinding.paramValueTextLayout)
//
//            layout.addView(rowBinding.root)
//        }
//
//        return AlertDialog.Builder(requireContext())
//            .setTitle(indicatorInput.indName)
//            .setView(layout)
//            .setPositiveButton("OK") { _, _ -> }
//            .setNegativeButton("Cancel") { _, _ -> }
//            .create()
//    }

//    override fun onStart() {
//        super.onStart()
//
//        (dialog as AlertDialog?)?.apply {
//            this.getButton(Dialog.BUTTON_POSITIVE)
//                .setOnClickListener {
//                    val paramsInput = paramTextLayouts.map { paramId ->
//                        paramId.editText?.text.toString()
//                    }
//                    if (isParamValuesValid(paramTextLayouts)) {
//                        args.indicator.indParamList = paramsInput.toMutableList()
//                        dismiss()
//                        findNavController().popBackStack(R.id.indicatorListFragment, true)
//                    }
//                }
//        }
//    }

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

//    companion object {
//        const val TAG = "InputParameterDialog"
//
//        @JvmStatic
//        fun newInstance(indicatorInput: IndicatorInput, primSec: Serializable?, isPopBackStack: Boolean) =
//            ParameterDialogFragment().apply {
//                arguments = Bundle().apply {
//                    putParcelable(INDICATOR_INPUT, indicatorInput)
////                    putString(IND_NAME, indName)
////                    putStringArrayList(PARAM_NAMES, paramNames)
//                    putSerializable(PRIM_SEC, primSec)
//                    putBoolean(IS_POP_BACK_STACK, isPopBackStack)
//                }
//            }
//
//        }
    }