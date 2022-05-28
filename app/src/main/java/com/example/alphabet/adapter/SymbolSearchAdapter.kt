package com.example.alphabet.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.alphabet.R
import com.example.alphabet.StockStatic
import com.example.alphabet.api.RetrofitInstance
import kotlinx.coroutines.launch

class SymbolSearchAdapter(context: Context, objects: List<StockStatic>) :
    ArrayAdapter<StockStatic>(context, 0, objects) {
    private var symbolSearchResults = objects.toList()

    override fun getItem(position: Int): StockStatic {
        return symbolSearchResults[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.symbol_search_row, parent, false)
        }
        val symbolSearchResult = getItem(position)
        val name = view!!.findViewById<TextView>(R.id.text_search_name)
        val symbol = view.findViewById<TextView>(R.id.text_search_symbol)
        val typeDisp = view.findViewById<TextView>(R.id.text_search_typeDisp)
        val exchDisp = view.findViewById<TextView>(R.id.text_search_exchDisp)

        name.text = symbolSearchResult.longname ?: symbolSearchResult.shortname
        symbol.text = symbolSearchResult.symbol
        typeDisp.text = symbolSearchResult.typeDisp
        exchDisp.text = symbolSearchResult.exchDisp
        return view
    }
}

fun Fragment.setUpSymbolSearch(actv: AutoCompleteTextView, onItemClick: (StockStatic) -> Unit) {
    actv.doAfterTextChanged { v ->
        val text = v?.toString() ?: ""
        lifecycleScope.launch {
            if (text.isNotEmpty()) {
                val r = try {
                    RetrofitInstance.api.searchSymbol(text)
                } catch (e: Exception) {
                    Log.e("Yahoo Finance API", e.toString())
                    return@launch
                }
                if (r.isSuccessful && r.body() != null) {
                    val results = r.body()!!.quotes
                    val adapter = SymbolSearchAdapter(requireContext(), results)
                    actv.setAdapter(adapter)
                    actv.setOnItemClickListener { _, _, i, _ ->
                        val stock = adapter.getItem(i)
                        actv.setText("")
                        onItemClick(stock)
                        actv.dismissDropDown()
                    }
                    actv.showDropDown()
                }
            }
        }
    }
}