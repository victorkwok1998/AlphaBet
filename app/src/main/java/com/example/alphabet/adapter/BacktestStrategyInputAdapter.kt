package com.example.alphabet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alphabet.R
import com.example.alphabet.database.StrategySchema

class BacktestStrategyInputAdapter(
    private val onDelete: (StrategySchema) -> Unit
) :
    ListAdapter<StrategySchema, BacktestStrategyInputAdapter.BacktestStrategyInputViewHolder>(
        BacktestResultDiffCallback()
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BacktestStrategyInputViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.backtest_strategy_input_row, parent, false)
        return BacktestStrategyInputViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BacktestStrategyInputViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.strategyText.text = currentItem.strategy.strategyName
        holder.deleteButton.setOnClickListener {
            onDelete(currentItem)
        }
    }

    inner class BacktestStrategyInputViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val strategyText: TextView = itemView.findViewById(R.id.text_strategy)
        val deleteButton: Button = itemView.findViewById(R.id.button_delete)
    }

    class BacktestResultDiffCallback: DiffUtil.ItemCallback<StrategySchema>() {
        override fun areItemsTheSame(
            oldItem: StrategySchema,
            newItem: StrategySchema
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: StrategySchema,
            newItem: StrategySchema
        ): Boolean {
            return oldItem == newItem
        }
    }
}

