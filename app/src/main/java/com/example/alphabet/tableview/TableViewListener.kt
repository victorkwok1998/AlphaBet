package com.example.alphabet.tableview

import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.tableview.TableView
import com.evrencoskun.tableview.listener.ITableViewListener

class TableViewListener(private val tableView: TableView): ITableViewListener {
    private val context = tableView.context

    override fun onCellClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
        TODO("Not yet implemented")
    }

    override fun onCellDoubleClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
        TODO("Not yet implemented")
    }

    override fun onCellLongPressed(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
        TODO("Not yet implemented")
    }

    override fun onColumnHeaderClicked(columnHeaderView: RecyclerView.ViewHolder, column: Int) {
        TODO("Not yet implemented")
    }

    override fun onColumnHeaderDoubleClicked(
        columnHeaderView: RecyclerView.ViewHolder,
        column: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun onColumnHeaderLongPressed(columnHeaderView: RecyclerView.ViewHolder, column: Int) {
        TODO("Not yet implemented")
    }

    override fun onRowHeaderClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) {
        TODO("Not yet implemented")
    }

    override fun onRowHeaderDoubleClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) {
        TODO("Not yet implemented")
    }

    override fun onRowHeaderLongPressed(rowHeaderView: RecyclerView.ViewHolder, row: Int) {
        TODO("Not yet implemented")
    }
}