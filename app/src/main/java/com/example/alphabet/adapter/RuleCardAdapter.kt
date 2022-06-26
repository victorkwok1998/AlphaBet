package com.example.alphabet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alphabet.*
import com.example.alphabet.databinding.ButtonIndicatorBinding
import com.example.alphabet.databinding.RuleCardBinding

class RuleCardAdapter(
    private val onIndicatorClicked: (IndicatorInput) -> Unit = {},
    private val onEditParamClicked: (IndicatorInput) -> Unit = {},
    private val onDeleteClicked: (MutableList<RuleInput>) -> Unit = {}
) :
    ListAdapter<RuleInput, RuleCardAdapter.RuleCardViewHolder>(RuleInputDiffCallback()) {
    private val condToChip = mapOf(
        Cond.CROSS_UP to R.id.cross_up_chip,
        Cond.CROSS_DOWN to R.id.cross_down_chip,
        Cond.OVER to R.id.over_chip,
        Cond.UNDER to R.id.under_chip
    )
    private val chipToCond = condToChip.entries.associate { (k, v) -> v to k }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleCardViewHolder {
        val binding = RuleCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RuleCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RuleCardViewHolder, position: Int) {
        val rule = getItem(position)
        holder.bind(rule)
    }

    fun submitListCustom(list: MutableList<RuleInput>) {
        if (list.isEmpty()) {
            list.add(RuleInput.getEmptyRule())
        }
        submitList(list)
    }

    inner class RuleCardViewHolder(private val binding: RuleCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(rule: RuleInput) {
            with(binding){
                bindButtonIndicator(layoutPrimary, rule.indInput1)
                bindButtonIndicator(layoutSecondary, rule.indInput2)

                buttonDeleteRule.setOnClickListener {
                    currentList.toMutableList()
                        .apply {
                            remove(rule)
                            submitListCustom(this)
                            onDeleteClicked(this)
                        }
                }

                if (rule.indInput1.indType == IndType.OTHER) {
                    groupCondition.visibility = View.GONE
                    layoutSecondary.root.visibility = View.GONE
                } else {
                    val checked = condToChip.getOrDefault(rule.condName, R.id.cross_up_chip)
                    conditionChipGroup.check(checked)
                    conditionChipGroup.setOnCheckedChangeListener { _, checkedId ->
                        chipToCond[checkedId]?.apply { rule.condName = this }
                    }
                }
            }
        }
        private fun bindButtonIndicator(
            buttonBinding: ButtonIndicatorBinding,
            ind: IndicatorInput
        ) {
            with(buttonBinding) {
                textIndicator.text = ind.toString()

                root.setOnClickListener {
                    if (ind.toString().isEmpty() || ind.indParamList.isEmpty()) {
                        onIndicatorClicked(ind)
                    } else {
                        onEditParamClicked(ind)
                    }
                }
            }
        }
    }
    class RuleInputDiffCallback: DiffUtil.ItemCallback<RuleInput>() {
        override fun areItemsTheSame(oldItem: RuleInput, newItem: RuleInput): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: RuleInput, newItem: RuleInput): Boolean {
            return oldItem.toString() == newItem.toString()
        }
    }
}