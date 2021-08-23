package com.example.alphabet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alphabet.databinding.RuleItemBinding

class StrategyAdapter(
    private val ruleList: List<RuleInput>,
    private val clickListener: OnItemClickListener,
    private val longClickListener: OnItemLongClickListener
) : RecyclerView.Adapter<StrategyAdapter.StrategyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StrategyViewHolder {
        val ruleItemBinding =
            RuleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StrategyViewHolder(ruleItemBinding)
    }

    override fun onBindViewHolder(holder: StrategyViewHolder, position: Int) {
        val currentRule = ruleList[position]
        holder.bind(currentRule, position)
    }

    override fun getItemCount(): Int {
        return ruleList.size
    }

    inner class StrategyViewHolder(private val ruleItemBinding: RuleItemBinding) :
        RecyclerView.ViewHolder(ruleItemBinding.root), View.OnClickListener,
        View.OnLongClickListener {

        init {
            ruleItemBinding.root.setOnClickListener(this)
            ruleItemBinding.root.setOnLongClickListener(this)
        }

        fun bind(ruleInput: RuleInput, position: Int) {
            ruleItemBinding.ruleTitle.text =
                itemView.context.getString(R.string.rule_title, position + 1)
            ruleItemBinding.ruleDetail.text = ruleInput.toString()
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                clickListener.onItemClick(position)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                longClickListener.onItemLongClick(position)
            }
            return true
        }
    }
}