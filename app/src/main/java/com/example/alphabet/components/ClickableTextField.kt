package com.example.alphabet.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphabet.R
import com.example.alphabet.ui.theme.grayBackground

@Composable
fun ClickableTextField(
    value: String,
    modifier: Modifier = Modifier,
    label: @Composable() (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    TextField(
        value = value,
        onValueChange = {},
        label = label,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        modifier = modifier,
        placeholder = placeholder,
        shape = RoundedCornerShape(20.dp),
        enabled = false,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = grayBackground,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
            disabledPlaceholderColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium),
            disabledLeadingIconColor = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.IconOpacity),
            disabledTrailingIconColor = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.IconOpacity),
            disabledLabelColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
        )
    )
}

@Preview
@Composable
fun PreviewStrategyText() {
    ClickableTextField(
        value = "RSI Strategy",
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_emoji_objects_24),
                contentDescription = null,
                tint = Color.Gray
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = Color.Gray
            )
        },
        label = {Text("Strategy")}
    )
}

@Preview
@Composable
fun PreviewDateText() {
    ClickableTextField(
        value = "07 Oct 20",
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_date_range_24),
                contentDescription = "date",
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null
            )
        },
        label = { Text("Start Date") }
    )
}