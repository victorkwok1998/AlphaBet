package com.example.alphabet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alphabet.database.StrategySchema

class StrategyListAdapter(
//    private var strategyList: List<StrategyInput>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<StrategyListAdapter.StrategyViewHolder>() {
    private var strategyList = listOf<StrategySchema>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StrategyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.double_row_layout, parent, false)
        return StrategyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StrategyViewHolder, position: Int) {
        val currentItem = strategyList[position].strategy
        holder.title.text = currentItem.strategyName
        holder.text.text = currentItem.des
    }

    override fun getItemCount(): Int = strategyList.size

    inner class StrategyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val title: TextView = itemView.findViewById(R.id.title_text)
        val text: TextView = itemView.findViewById(R.id.body_text)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION)
                listener.onItemClick(position)
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
        fun onItemClick(position: Int)
    }
}