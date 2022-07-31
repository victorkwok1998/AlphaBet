package com.example.alphabet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alphabet.database.BacktestResultSchema
import com.example.alphabet.databinding.BacktestResultRowBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class BacktestResultAdapter(
    private val context: Context,
    private val listener: OnItemClickListener,
//    private val onDeleteClicked: (BacktestResultSchema) -> Unit,
//    private val onRerunClicked: (BacktestResultSchema) -> Unit
) :
    ListAdapter<BacktestResultSchema, BacktestResultAdapter.BacktestResultViewHolder>(BacktestResultDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BacktestResultViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.backtest_row, parent, false)

        return BacktestResultViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BacktestResultViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.symbolText.text = currentItem.backtestResult.backtestInput.stock.symbol
        holder.strategyText.text = currentItem.backtestResult.backtestInput.strategyInput.strategyName

//        holder.moreButton.setOnClickListener {
//            BottomSheetDialog(context).apply {
//                val binding = BacktestResultRowBottomSheetBinding.inflate(LayoutInflater.from(context), null, false)
//                binding.deleteRow.setOnClickListener {
////                    notifyItemRemoved(position)
////                    notifyItemRangeChanged(position, itemCount - position)
//                    onDeleteClicked(currentItem)
//                    this.dismiss()
//                }
//                binding.rerunRow.setOnClickListener {
//                    onRerunClicked(currentItem)
//                }
//                this.setContentView(binding.root)
//                this.show()
//            }
//        }

        val cashFlow = currentItem.backtestResult.getCashFlow()
//        val date = currentItem.backtestResult.date
        if (cashFlow.isNotEmpty()) {
            holder.dateRangeText.text = getBacktestPeriodString(currentItem.backtestResult, context)
            val pnl = cashFlow.last() - 1
            holder.returnText.text = MyApplication.pct.format(pnl)
            if (pnl < 0) {
                holder.returnText.background.setTint(ContextCompat.getColor(context, R.color.red))
            } else {
                holder.returnText.background.setTint(ContextCompat.getColor(context, R.color.green))
            }
        }
    }

//    override fun getItemCount() = backtestResults.size

    inner class BacktestResultViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        val symbolText: TextView = itemView.findViewById(R.id.symbol_text)
        val strategyText: TextView = itemView.findViewById(R.id.strategy_name_text)
        val dateRangeText: TextView = itemView.findViewById(R.id.date_range_text)
        val returnText: TextView = itemView.findViewById(R.id.return_text)
//        val moreButton: Button = itemView.findViewById(R.id.backtest_row_more_button)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION)
                listener.onItemClick(position)
        }

        override fun onLongClick(p0: View?): Boolean {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION)
                listener.onItemLongClick(position)
            return true
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

class BacktestResultDiffCallback: DiffUtil.ItemCallback<BacktestResultSchema>() {
    override fun areItemsTheSame(
        oldItem: BacktestResultSchema,
        newItem: BacktestResultSchema
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: BacktestResultSchema,
        newItem: BacktestResultSchema
    ): Boolean {
        return oldItem == newItem
    }
}}