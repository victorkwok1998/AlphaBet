package com.example.alphabet.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.alphabet.Cond
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DropDownTextField(
    value: String,
    possibleValues: List<String>,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(targetValue = if(isExpanded) 180f else 0f)
    var selectedText by remember { mutableStateOf(value) }
    var dropDownWidth by remember { mutableStateOf(Size.Zero) }
    Column(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .onGloballyPositioned { dropDownWidth = it.size.toSize() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                selectedText,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier
                    .padding(vertical = 10.dp)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "",
                modifier = Modifier
                    .rotate(rotationState)
                    .alpha(ContentAlpha.medium)
            )
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { dropDownWidth.width.toDp() })
        ) {
            possibleValues.forEach {
                DropdownMenuItem(onClick = {
                    selectedText = it
                    isExpanded = false
                    onChange(it)
                }) {
                    Text(it)
                }
            }
        }
    } 
}

@Preview
@Composable
fun PreviewDropDownTextField() {
    val options = Cond.values().map { it.value }
    DropDownTextField(
        options[0],
        options,
        {}
    )
}