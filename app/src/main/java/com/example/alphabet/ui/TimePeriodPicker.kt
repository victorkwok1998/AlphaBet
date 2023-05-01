package com.example.alphabet.ui

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.alphabet.R
import com.example.alphabet.createCalendar
import com.example.alphabet.databinding.BacktestPeriodBinding
import com.example.alphabet.databinding.DialogTimePeriodBinding
import com.example.alphabet.getYesterday
import com.example.alphabet.util.Constants
import com.google.android.material.textfield.TextInputEditText
import java.util.*

private fun setUpDate(
    text: TextInputEditText,
    datePicker: DatePicker,
    datePicker2: DatePicker,
    liveData: MutableLiveData<Calendar>
) {
    text.setOnClickListener {
        datePicker.isVisible = !datePicker.isVisible
        datePicker2.isVisible = false
        datePicker.setOnDateChangedListener { _, year, month, day ->
            liveData.value = createCalendar(year, month, day)
        }
    }
}

private fun timePeriodShortCut(
    year: Int,
    startLiveData: MutableLiveData<Calendar>,
    endLiveData: MutableLiveData<Calendar>
) {
    val end = getYesterday()
    val start = Calendar.getInstance().apply {
        time = end.time
        add(Calendar.YEAR, -year)
    }
    startLiveData.value = start
    endLiveData.value = end
}

private fun updateDateComponent(calendar: Calendar, text: TextInputEditText, datePicker: DatePicker) {
    text.setText(Constants.sdfLong.format(calendar.time))
    datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
}

fun DialogTimePeriodBinding.setTimePeriod(
    startLiveData: MutableLiveData<Calendar>,
    endLiveData: MutableLiveData<Calendar>,
    viewLifecycleOwner: LifecycleOwner
): DialogTimePeriodBinding {
    with(this) {
        startLiveData.observe(viewLifecycleOwner) {
            updateDateComponent(it, textStartDate, datePickerStart)
        }
        endLiveData.observe(viewLifecycleOwner) {
            updateDateComponent(it, textEndDate, datePickerEnd)
        }
        setUpDate(textStartDate, datePickerStart, datePickerEnd, startLiveData)
        setUpDate(textEndDate, datePickerEnd, datePickerStart, endLiveData)

        button1y.setOnClickListener {
            timePeriodShortCut(1, startLiveData, endLiveData)
        }
        button2y.setOnClickListener {
            timePeriodShortCut(2, startLiveData, endLiveData)
        }
        button5y.setOnClickListener {
            timePeriodShortCut(5, startLiveData, endLiveData)
        }
        button10y.setOnClickListener {
            timePeriodShortCut(10, startLiveData, endLiveData)
        }
    }
    return this
}

fun setDatePicker(context: Context, editText: EditText, c: Calendar, onDateChange: (Calendar) -> Unit) {
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)

    if (editText.text.toString().isEmpty()) {
        editText.setText(Constants.sdfLong.format(c.time))
    }

    editText.setOnClickListener {
        val dpd = DatePickerDialog(context, R.style.MySpinnerDatePickerStyle, { _, mYear, mMonth, mDay ->
            val cal = createCalendar(mYear, mMonth, mDay)
            editText.setText(Constants.sdfLong.format(cal.time))
            onDateChange(cal)
        }, year, month, day)
        dpd.show()
    }
}

fun BacktestPeriodBinding.setTimePeriod(
    context: Context,
    startLiveData: MutableLiveData<Calendar>,
    endLiveData: MutableLiveData<Calendar>,
    viewLifecycleOwner: LifecycleOwner
) {
    startLiveData.observe(viewLifecycleOwner) {
        this.textStartDate.setText(Constants.sdfLong.format(it.time))
    }
    endLiveData.observe(viewLifecycleOwner) {
        this.textEndDate.setText(Constants.sdfLong.format(it.time))
    }
    setDatePicker(context, this.textStartDate, startLiveData.value!!) {
        startLiveData.value = it
    }
    setDatePicker(context, this.textEndDate, endLiveData.value!!) {
        endLiveData.value = it
    }
    button1y.setOnClickListener {
        timePeriodShortCut(1, startLiveData, endLiveData)
    }
    button2y.setOnClickListener {
        timePeriodShortCut(2, startLiveData, endLiveData)
    }
    button5y.setOnClickListener {
        timePeriodShortCut(5, startLiveData, endLiveData)
    }
    button10y.setOnClickListener {
        timePeriodShortCut(10, startLiveData, endLiveData)
    }
}
