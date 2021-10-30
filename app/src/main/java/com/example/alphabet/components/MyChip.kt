package com.example.alphabet.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphabet.ui.theme.grayBackground

@Composable
fun MyChip(
    text: String,
    selected: Boolean,
    OnClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if(selected) MaterialTheme.colors.primary else grayBackground,
        modifier = modifier.clickable { OnClick() }
    ) {
        Text(
            text,
            style = MaterialTheme.typography.body1,
            color = if(selected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Preview
@Composable
fun PreviewMyChipSelected() {
    MyChip("Value", true, {})
}

@Preview
@Composable
fun PreviewMyChipNotSelected() {
    MyChip("Indicator", false, {})
}