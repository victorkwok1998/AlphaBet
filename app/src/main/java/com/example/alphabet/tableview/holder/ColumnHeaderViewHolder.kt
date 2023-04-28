package com.example.alphabet.tableview.holder

import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.evrencoskun.tableview.ITableView
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder
import com.evrencoskun.tableview.sort.SortState
import com.example.alphabet.R
import com.example.alphabet.tableview.model.Cell

class ColumnHeaderViewHolder(itemView: View, private val tableView: ITableView) :
    AbstractSorterViewHolder(itemView) {
    private val column_header_textview: TextView = itemView.findViewById(R.id.column_header_textView)
    private val column_header_sortButton: ImageButton  = itemView.findViewById(R.id.column_header_sortButton)
    private val column_header_container: LinearLayout  = itemView.findViewById(R.id.column_header_container)

    init {
        column_header_sortButton.setOnClickListener {
            when(sortState) {
                SortState.ASCENDING -> {
                    tableView.sortColumn(bindingAdapterPosition, SortState.DESCENDING)
                }
                SortState.DESCENDING -> {
                    tableView.sortColumn(bindingAdapterPosition, SortState.ASCENDING)
                }
                else -> tableView.sortColumn(bindingAdapterPosition, SortState.DESCENDING)
            }
        }
    }

    override fun onSortingStatusChanged(sortState: SortState) {
        super.onSortingStatusChanged(sortState)
        column_header_container.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        controlSortState(sortState)
        column_header_textview.requestLayout()
        column_header_sortButton.requestLayout()
        column_header_container.requestLayout()
        itemView.requestLayout()
    }
    fun setColumnHeader(columnHeader: Cell) {
        column_header_textview.text = columnHeader.getData().toString()
        column_header_container.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        column_header_textview.requestLayout()
    }
    private fun controlSortState(sortState: SortState) {
        when (sortState) {
            SortState.ASCENDING -> {}
            SortState.DESCENDING -> {}
            else -> {}
        }
    }
}