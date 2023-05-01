package com.example.alphabet.tableview.holder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.example.alphabet.R
import com.example.alphabet.tableview.model.Cell

class CellViewHolder(itemView: View) : AbstractViewHolder(itemView) {
    private val cell_textview = itemView.findViewById<TextView>(R.id.cell_data)
    private val cell_container = itemView.findViewById<LinearLayout>(R.id.cell_container)
    fun setCell(cell: Cell?) {
        cell_textview.text = cell?.getData()
        cell_container.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        cell_textview.requestLayout()
    }
}