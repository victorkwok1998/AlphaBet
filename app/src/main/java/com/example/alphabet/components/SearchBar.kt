package com.example.alphabet.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BasicTextFieldLayout(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    backgroundColor: Color = Color.Transparent,
    placeholder: String?
) {
    Surface(
        color = backgroundColor,
        shape = shape,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Column(
                    Modifier.size(40.dp),
                    verticalArrangement = Arrangement.Center
                ) { leadingIcon() }
            }
            Column(Modifier.weight(1f)) {
                Box() {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (placeholder != null && value.isEmpty()) {
                        Text(
                            placeholder,
                            color = Color.Gray,
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                }

            }
            if (trailingIcon != null) {
                Column(
                    Modifier.size(40.dp),
                    verticalArrangement = Arrangement.Center
                ) { trailingIcon() }
            }
        }
    }
}

@Composable
fun SearchBar(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    BasicTextFieldLayout(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface
            )
        },
        placeholder = "Search Strategy",
        backgroundColor = Color.Gray.copy(alpha = 0.2f)
    )
}

@Preview
@Composable
fun PreviewSearchBar() {
    SearchBar(value = "", onValueChange = {})
}