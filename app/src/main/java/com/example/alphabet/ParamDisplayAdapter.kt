package com.example.alphabet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ParamDisplayAdapter(context: Context, private val paramData: List<Pair<String, String>>) :
    ArrayAdapter<Pair<String, String>>(context, R.layout.param_display_row, paramData) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.param_display_row, parent, false)
        val param = getItem(position)
        if(param != null) {
            view.findViewById<TextView>(R.id.param_name_label).text = param.first
            view.findViewById<TextView>(R.id.param_value_text).text = param.second
        }
        return view
    }
}