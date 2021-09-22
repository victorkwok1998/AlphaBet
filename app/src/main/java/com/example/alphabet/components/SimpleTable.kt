package com.example.alphabet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SimpleTable(
    header: List<String>,
    data: List<List<String>>,
    cellModifier: List<Modifier> = header.map { Modifier }, // per column
    weights: List<Float> = header.map { 1f / header.size }
) {
    Column(Modifier.padding(vertical = 10.dp)) {
        // header
        Row(Modifier.padding(vertical = 10.dp)) {
            header.zip(weights).forEach { (t, w) ->
                TableCell(text = t, weight = w, isHeader = true)
            }
        }
        Divider(thickness = 1.dp)
        data.forEach { row ->
            Row(Modifier.padding(vertical = 10.dp)) {
                row.forEachIndexed { index, t ->
                    TableCell(text = t, weight = weights[index], modifier = cellModifier[index])
                }
//                row.zip(weights).forEach { (t, w) ->
//                    TableCell(text = t, weight = w, modifier = cellModifier)
//                }
            }
            Divider(thickness = 1.dp)
        }
        if (data.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No data available",
                    style = MaterialTheme.typography.subtitle1,
                    color = Color.Gray,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSimpleTable() {
    SimpleTable(
        header = listOf("Symbol", "Strategy"),
        data = listOf(listOf("INTC", "RSI"), listOf("TSLA", "SMA")),
    )
}