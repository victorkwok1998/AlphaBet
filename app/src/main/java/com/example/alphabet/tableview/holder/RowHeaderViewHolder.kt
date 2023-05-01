package com.example.alphabet.tableview.holder

import android.view.View
import android.widget.TextView
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.example.alphabet.R

class RowHeaderViewHolder(itemView: View): AbstractViewHolder(itemView) {
    val row_header_textview = itemView.findViewById<TextView>(R.id.row_header_textview)
}