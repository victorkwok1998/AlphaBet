package com.example.alphabet.ui

import android.content.Context

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.core.view.updateLayoutParams
import com.example.alphabet.BacktestResult
import com.example.alphabet.R
import com.example.alphabet.databinding.ChipLegendBinding
import com.example.alphabet.plotColors
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

fun setChipGroup(
    context: Context,
    backtestResultList: Array<BacktestResult>,
    chipGroup: ChipGroup,
    checkedIndex: Int = -1,
    onCheck: (List<Boolean>) -> Unit
) {
    val backtestNames = backtestResultList.map { it.backtestInput.getShortName() }
    val inflater = LayoutInflater.from(context)

    chipGroup.removeAllViews()
    backtestNames.forEachIndexed { i, name ->
        val chip = ChipLegendBinding.inflate(inflater, chipGroup, false).root
        chip.text = name
        if (checkedIndex == -1 || i == checkedIndex) {
            chip.isChecked = true
        }
        chip.setChipIconTintResource(plotColors[i % plotColors.size])
        chip.setOnCheckedChangeListener { _, _ ->
            val enabledLines = chipGroup.checkedList()
            if (enabledLines.any { it })
                onCheck(enabledLines)
        }
        chip.setOnCloseIconClickListener {
            if (it.layoutParams.width == WRAP_CONTENT) {
                it.updateLayoutParams { width = context.resources.getDimension(R.dimen.chip_legend_width).toInt() }
                chip.closeIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_right_24)
            }
            else {
                it.updateLayoutParams { width = WRAP_CONTENT }
                chip.closeIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_left_24)
            }
        }
        chipGroup.addView(chip)
    }
}

fun ChipGroup.checkedIndex() = this.checkedList().indexOf(true)

fun ChipGroup.checkedList() = List(this.size) { i -> (this[i] as Chip).isChecked}