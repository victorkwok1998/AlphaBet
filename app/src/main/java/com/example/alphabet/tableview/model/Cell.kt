package com.example.alphabet.tableview.model

import com.evrencoskun.tableview.filter.IFilterableModel
import com.evrencoskun.tableview.sort.ISortableModel

open class Cell(private val mId: String, private val mData: Any?): ISortableModel, IFilterableModel {
    private val mFilterKeyword = mData.toString()

    override fun getId(): String {
        return mId
    }

    override fun getContent(): Any? {
        return mData
    }

    fun getData() = mData

    override fun getFilterableKeyword(): String {
        return mFilterKeyword
    }

}