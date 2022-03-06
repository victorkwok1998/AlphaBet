package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.activityViewModels
import com.example.alphabet.MyApplication.Companion.sdfLong
import com.example.alphabet.databinding.DialogTimePeriodBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class TimePeriodBottomSheetFragment: BottomSheetDialogFragment() {
    private val viewModel: StrategyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dateBinding = DialogTimePeriodBinding.inflate(inflater, container, false)


        with(dateBinding) {
            viewModel.start.observe(viewLifecycleOwner) {
                updateDateComponent(it, textStartDate, datePickerStart)
            }
            viewModel.end.observe(viewLifecycleOwner) {
                updateDateComponent(it, textEndDate, datePickerEnd)
            }

            setUpDate(textStartDate, datePickerStart, datePickerEnd) {
                viewModel.start.value = it
            }
            setUpDate(textEndDate, datePickerEnd, datePickerStart) {
                viewModel.end.value = it
            }
            button1y.setOnClickListener {
                timePeriodShortCut(1)
            }
            button2y.setOnClickListener {
                timePeriodShortCut(2)
            }
            button5y.setOnClickListener {
                timePeriodShortCut(5)
            }
            button10y.setOnClickListener {
                timePeriodShortCut(10)
            }
        }

        return dateBinding.root
    }

    private fun setUpDate(
        text: TextInputEditText,
        datePicker: DatePicker,
        datePicker2: DatePicker,
        onDateChange: (Calendar) -> Unit
    ) {
        text.setOnClickListener {
            switchVisibility(datePicker)
            datePicker2.visibility = View.GONE
            datePicker.setOnDateChangedListener { _, year, month, day ->
                val calendar = createCalendar(year, month, day)
                onDateChange(calendar)
            }
        }
    }

    private fun timePeriodShortCut(year: Int) {
        val end = viewModel.defaultEnd
        val start = Calendar.getInstance().apply {
            time = end.time
            add(Calendar.YEAR, -year)
        }
        viewModel.start.value = start
        viewModel.end.value = end
    }
    private fun updateDateComponent(calendar: Calendar, text: TextInputEditText, datePicker: DatePicker) {
        text.setText(sdfLong.format(calendar.time))
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }
}