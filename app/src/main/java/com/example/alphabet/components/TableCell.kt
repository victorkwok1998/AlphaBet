package com.example.alphabet.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    modifier: Modifier = Modifier,
    isHeader: Boolean = false,
    textAlign: TextAlign = TextAlign.Left
) {
    val fontWeight = when {
        isHeader -> FontWeight.Bold
        else -> FontWeight.Normal
    }
    Text(
        text = text,
        modifier = modifier.weight(weight),
        fontWeight = fontWeight,
        style = MaterialTheme.typography.subtitle1,
        textAlign = textAlign
    )
}