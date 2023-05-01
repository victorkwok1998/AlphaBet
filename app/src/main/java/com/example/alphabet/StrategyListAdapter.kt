package com.example.alphabet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.alphabet.database.StrategySchema
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class StrategyListAdapter(
//    private var strategyList: List<StrategyInput>,
    private val context: Context,
    private val listener: OnItemClickListener,
    private val enableCheckBox: Boolean = false,
    private val checkedStrategy: MutableMap<Long, StrategySchema>? = null,
) :
    RecyclerView.Adapter<StrategyListAdapter.StrategyViewHolder>() {
    private var strategyList = listOf<StrategySchema>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StrategyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_strategy_row, parent, false)
        return StrategyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StrategyViewHolder, position: Int) {
        val currentItem = strategyList[position]
        holder.title.text = currentItem.strategy.strategyName
//        holder.text.text = context.getString(R.string.indicator_used, currentItem.strategy.indicatorUsed().joinToString(", "))
        holder.chipGroup.removeAllViews()
        currentItem.strategy.indicatorUsed()
            .distinctBy { it.indName }
            .forEach {
                val chip = LayoutInflater.from(context).inflate(R.layout.chip_indicator, holder.chipGroup, false) as Chip
                chip.text = it.indName
                when(it.indType) {
                    IndType.TECHNICAL -> {
                        chip.chipIcon = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_baseline_candlestick_chart_24
                        )
                        chip.setChipIconTintResource(R.color.plotColor1)
                    }
                    IndType.PRICE -> {
                        chip.chipIcon = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_baseline_attach_money_24
                        )
                        chip.setChipIconTintResource(R.color.plotColor7)
                    }
                    IndType.CONSTANT -> {
                        chip.chipIcon =
                            ContextCompat.getDrawable(context, R.drawable.ic_baseline_30fps_24)
                        chip.setChipIconTintResource(R.color.plotColor5)
                    }
                    else -> {}
                }
                holder.chipGroup.addView(chip)
            }

        holder.label.text = currentItem.strategy.strategyType

        if (enableCheckBox) {
            checkedStrategy?.apply {
                holder.checkBox.isChecked = this.contains(currentItem.id)
            }
        } else {
            holder.checkBox.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = strategyList.size

    inner class StrategyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val title: TextView = itemView.findViewById(R.id.text_title)
//        val text: TextView = itemView.findViewById(R.id.text_body)
        val chipGroup: ChipGroup = itemView.findViewById(R.id.chip_group_indicators)
        val label: TextView = itemView.findViewById(R.id.text_label)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION)
                listener.onItemClick(position, checkBox)
        }
    }

    fun updateList(newList: List<StrategySchema>) {
        strategyList = newList
        notifyDataSetChanged()
    }

    fun getList(): List<StrategySchema> {
        return strategyList
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, checkBox: CheckBox?)
    }
}