package com.example.alphabet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IndicatorAdapter(
    private val indicatorList: List<IndicatorStatic>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<IndicatorAdapter.IndicatorViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndicatorViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.double_row_layout, parent, false)

        return IndicatorViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IndicatorViewHolder, position: Int) {
        val currentItem = indicatorList[position]
        holder.indNameView.text = currentItem.indName
        if (currentItem.paramName.isEmpty()) {
            holder.indParamView.text = "No Parameter"
        } else {
            holder.indParamView.text = currentItem.paramName.joinToString(", ")
        }
    }

    override fun getItemCount() = indicatorList.size

    inner class IndicatorViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val indNameView: TextView = itemView.findViewById(R.id.title_text)
        val indParamView: TextView = itemView.findViewById(R.id.body_text)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION)
                listener.onItemClick(position)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}