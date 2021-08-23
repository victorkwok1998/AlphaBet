package com.example.alphabet.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SimpleTable(
    header: List<String>,
    data: List<List<String>>,
    weights: List<Float> = List(header.size) { 1f / header.size }
) {
    Column(Modifier.padding(10.dp)) {
        // header
        Row(Modifier.padding(vertical = 10.dp)) {
            header.zip(weights).forEach { (t, w) ->
                TableCell(text = t, weight = w, isHeader = true)
            }
        }
        Divider(thickness = 1.dp)
        data.forEach { row ->
            Row(Modifier.padding(vertical = 10.dp)) {
                row.zip(weights).forEach { (t, w) ->
                    TableCell(text = t, weight = w)
                }
            }
            Divider(thickness = 1.dp)
        }
        if (data.isEmpty()) {
            Text(text = "No data available", style = MaterialTheme.typography.subtitle1, color = Color.Gray)
        }
    }
}