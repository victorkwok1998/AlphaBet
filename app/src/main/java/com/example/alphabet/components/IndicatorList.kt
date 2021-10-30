package com.example.alphabet.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IndicatorList(
    indicators: List<String>,
    onClick: (Int) -> Unit
) {
    MyCard(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            Modifier.padding(horizontal = 20.dp)
        ) {
            indicators.forEachIndexed { index, value ->
                Text(
                    value,
                    style = MaterialTheme.typography.subtitle1,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth()
                        .clickable { onClick(index) }
                )
                Divider()
            }
        }
    }
}

@Preview
@Composable
fun PreviewIndicatorList() {
    IndicatorList(indicators = listOf("RSI", "EMA", "SMA"), onClick = {})
}