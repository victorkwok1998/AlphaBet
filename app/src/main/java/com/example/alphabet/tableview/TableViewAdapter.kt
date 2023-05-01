package com.example.alphabet.tableview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.example.alphabet.R
import com.example.alphabet.tableview.holder.CellViewHolder
import com.example.alphabet.tableview.holder.ColumnHeaderViewHolder
import com.example.alphabet.tableview.holder.RowHeaderViewHolder
import com.example.alphabet.tableview.model.Cell

class TableViewAdapter(private val tableViewModel: TableViewModel) :
    AbstractTableAdapter<Cell, Cell, Cell>() {
    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.table_view_cell_layout, parent, false)
        return CellViewHolder(layout)
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.table_view_column_header_layout, parent, false)
        return ColumnHeaderViewHolder(layout, tableView)
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.table_view_row_header_layout, parent, false)
        return RowHeaderViewHolder(layout)
    }

    override fun onCreateCornerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.table_view_corner_layout, parent, false)
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: Cell?,
        rowPosition: Int
    ) {
        (holder as RowHeaderViewHolder).row_header_textview.text = rowHeaderItemModel?.getData()
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: Cell?,
        columnPosition: Int
    ) {
        (holder as ColumnHeaderViewHolder).setColumnHeader(columnHeaderItemModel)
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: Cell?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        (holder as CellViewHolder).setCell(cellItemModel)
    }

    fun setAllItemsFromVM() {
        setAllItems(
            tableViewModel.getColumnHeaderList(),
            tableViewModel.getRowHeaderList(),
            tableViewModel.getCellList()
        )
    }
}