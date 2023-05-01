package com.example.alphabet.tableview.model

import com.evrencoskun.tableview.filter.IFilterableModel
import com.evrencoskun.tableview.sort.ISortableModel
import com.example.alphabet.util.Constants

enum class CellFormat {
    STRING,
    FLOAT,
    PCT
}

open class Cell(
    private val mId: String,
    private val mData: Any?,
    private val cellFormat: CellFormat = CellFormat.STRING
) : ISortableModel, IFilterableModel {
    private val mFilterKeyword = mData.toString()

    override fun getId(): String {
        return mId
    }

    override fun getContent(): Any? {
        return mData
    }

    fun getData(): String {
        return when (cellFormat) {
            CellFormat.PCT -> Constants.pct.format(mData)
            CellFormat.FLOAT -> Constants.twoDp.format(mData)
            else -> mData.toString()
        }
    }

    override fun getFilterableKeyword(): String {
        return mFilterKeyword
    }

}