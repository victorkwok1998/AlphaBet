package com.example.alphabet

import android.content.Context
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.databinding.CreateStrategyBinding
import com.example.alphabet.databinding.FragmentCreateRuleBinding
import com.example.alphabet.databinding.IndicatorParamBinding
import com.example.alphabet.databinding.SelectIndicatorBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.Rule
import org.ta4j.core.TradingRecord
import org.ta4j.core.analysis.CashFlow
import org.ta4j.core.rules.BooleanRule
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*



fun setStratList(context: Context?, createStrategyBinding: CreateStrategyBinding, adapter: StrategyAdapter) {
    createStrategyBinding.stratList.run {
        this.layoutManager = LinearLayoutManager(context)
        this.adapter = adapter
        this.setHasFixedSize(true)
    }
}

fun setParamInput(layout: LinearLayout, indParamList: List<Int>?, paramNameList: List<String>, layoutInflater: LayoutInflater) {
    layout.removeAllViews()
    paramNameList.forEachIndexed { i, v ->
        IndicatorParamBinding.inflate(layoutInflater, layout, false).apply {
            this.paramValueText.hint = v
            // fill param list if not null
            indParamList?.let { this.paramValueText.editText?.setText(it[i].toString()) }
            layout.addView(this.root)
        }
    }
}



//fun showParamListEditText(
//    parent: AdapterView<*>?,
//    position: Int,
//    indToParam: Map<String, List<String>>,
//    paramListLayout: LinearLayout,
//    layoutInflater: LayoutInflater
//) {
//    val selected = parent?.getItemAtPosition(position).toString()
//    paramListLayout.removeAllViews()
//    indToParam[selected]?.forEach {
//        IndicatorParamBinding.inflate(layoutInflater, null, false).apply {
//            this.paramValueText.hint = it
//            paramListLayout.addView(this.root)
//        }
//    }
//}

fun readParamListFromLayout(layout: LinearLayout): List<Int> {
    return layout.run {
        List(this.childCount) { i ->
            val tv: TextInputLayout = layout.getChildAt(i).findViewById(R.id.param_value_text)
            tv.editText!!.text.toString().toInt()
        }
    }
}

fun readIndicatorInput(selIndBinding: SelectIndicatorBinding): IndicatorInput {
//    val indType = when (selIndBinding.typeRadioGroup.checkedRadioButtonId) {
//        R.id.specific_value_button -> "Value"
//        R.id.indicator_button -> "Indicator"
//        else -> "Price"
//    }
    val indName = when (selIndBinding.typeRadioGroup.checkedButtonId) {
        R.id.specific_value_button -> selIndBinding.specificValueText.text.toString()
        R.id.indicator_button -> selIndBinding.indicatorSpinner.selectedItem.toString()
        else -> "Close Price"
    }
    val indParamList = when (selIndBinding.typeRadioGroup.checkedButtonId) {
        R.id.indicator_button -> readParamListFromLayout(selIndBinding.indicatorParamListLayout)
        else -> listOf()
    }

    return IndicatorInput(IndType.fromId(selIndBinding.typeRadioGroup.checkedButtonId), indName, indParamList)
}

fun createRuleInput(
    selIndBinding1: SelectIndicatorBinding,
    selIndBinding2: SelectIndicatorBinding,
    createRuleBinding: FragmentCreateRuleBinding
): RuleInput {
    val ind1 = readIndicatorInput(selIndBinding1)
    val ind2 = readIndicatorInput(selIndBinding2)
    return RuleInput(ind1, ind2, Cond.fromId(createRuleBinding.conditionRadioGroup.checkedButtonId))
}

fun strategyActionItemClicked(
    mode: ActionMode?,
    item: MenuItem?,
    createStrategyBinding: CreateStrategyBinding,
    ruleInputList: MutableList<RuleInput>,
    strategyAdapter: StrategyAdapter
): Boolean {
    return when (item?.itemId) {
        R.id.action_delete -> {
            val checked = mutableListOf<Int>()
            ruleInputList.forEachIndexed { i, _ ->
                val card =
                    createStrategyBinding.stratList.layoutManager!!.findViewByPosition(i) as MaterialCardView
                if (card.isChecked)
                    checked.add(i)
            }
            for ((count, i) in checked.withIndex()) {
                ruleInputList.removeAt(i - count)
            }
            strategyAdapter.notifyDataSetChanged()
            mode?.finish()
            true
        }
        else -> false
    }
}

fun aggRule(indRules: List<Rule>, nonIndRules: List<Rule>): Rule {
    return when {
        indRules.isNotEmpty() && nonIndRules.isNotEmpty() -> {
            nonIndRules.reduce{ res, rule -> res.or(rule)}.or(indRules.reduce { res, rule -> res.and(rule)})
        }
        indRules.isEmpty() && nonIndRules.isNotEmpty() -> nonIndRules.reduce{ res, rule -> res.or(rule)}
        indRules.isNotEmpty() && nonIndRules.isEmpty() -> indRules.reduce { res, rule -> res.and(rule)}
        else -> BooleanRule(true)
    }
}

fun getCashFlow(series: BaseBarSeries, tradingRecord: TradingRecord): List<Float> {
    val cashFlow = CashFlow(series, tradingRecord)
    return List(cashFlow.size) {i ->  cashFlow.getValue(i).floatValue()}
}

fun createCalandar(year: Int, month: Int, date: Int): Calendar {
    return Calendar.getInstance().also { it.set(year, month, date) }
}

//fun parseDate(date: String): Calendar? {
//    return if (date == "") {
//        null
//    } else {
//        val year = date.substring(0, 4).toInt()
//        val month = date.substring(4, 6).toInt()
//        val day = date.substring(6, 8).toInt()
//        createCalandar(year, month, day)
//    }
//}

fun getJsonDataFromAsset(context: Context, fileName: String): String {
    val jsonString: String
    try {
        jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        throw ioException
    }
    return jsonString
}

fun readFile(file: File): String {
    return FileInputStream(file).bufferedReader().use { it.readText() }
}