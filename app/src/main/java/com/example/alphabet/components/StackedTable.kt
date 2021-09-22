package com.example.alphabet.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * @param data: order: primaryIndex -> secondaryIndex
 */
@Composable
fun StackedTable(
    primaryIndex: List<String>,
    secondaryIndex: List<String>,
    data: List<List<String>>,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        primaryIndex.forEachIndexed { index, headerText ->
            Row(Modifier.padding(vertical = 8.dp)) {
                Text(
                    headerText,
                    modifier = Modifier.weight(2f),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            secondaryIndex.zip(data[index]).forEach { (s, d) ->
                Row(Modifier.padding(vertical = 2.dp)) {
                    Text(s, modifier = Modifier
                        .weight(2f)
                        .padding(start = 20.dp), style = MaterialTheme.typography.subtitle1)
                    Text(d, modifier = Modifier.weight(1f))
                }
            }
            if (index != primaryIndex.lastIndex)
                Divider()
        }
    }
}

@Preview
@Composable
fun PreviewStackedTable() {
    StackedTable(
        primaryIndex = listOf(
            "Total PnL",
            "Maximum Drawdown",
            "Winning Trades",
            "Losing Trades"
        ),
        secondaryIndex = listOf("INTC RSI Strategy", "TSLA RSI Strategy"),
        data = listOf(
            listOf("10%", "20%"),
            listOf("30%", "40%"),
            listOf("5", "2"),
            listOf("3", "4")
        )
    )
}