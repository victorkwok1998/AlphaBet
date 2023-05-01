package com.example.alphabet.tableview

import com.example.alphabet.tableview.model.Cell
import com.example.alphabet.tableview.model.CellFormat

class TableViewModel(
    private val columnNames: List<String>,
    private val rowNames: List<String>,
    private val data: List<List<Any>>,
    private val cellFormatList: List<CellFormat>
) {
    fun getColumnHeaderList() = columnNames.mapIndexed { index, s -> Cell(index.toString(), s) }
    fun getRowHeaderList() = rowNames.mapIndexed { index, s -> Cell(index.toString(), s) }
    fun getCellList() = data.mapIndexed { i, row ->
        row.zip(cellFormatList).mapIndexed {j, (x, cellFormat) ->
            Cell("$i$j", x, cellFormat)
        }
    }
}