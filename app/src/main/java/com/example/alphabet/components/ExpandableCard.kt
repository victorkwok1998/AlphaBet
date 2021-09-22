package com.example.alphabet.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@Composable
fun ExpandableCard(
    text: String,
    defaultIsExpanded: Boolean = false,
    addOnClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(defaultIsExpanded) }
    val rotationState by animateFloatAsState(targetValue = if(isExpanded) 180f else 0f)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(easing = LinearOutSlowInEasing, durationMillis = 300)
            )
            .padding(10.dp),
        shape = RoundedCornerShape(10.dp),
        onClick = { isExpanded = !isExpanded }) {
        Column(Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.weight(0.6f)
                )
                addOnClick?.apply {
                    IconButton(
                        modifier = Modifier
                            .weight(0.1f)
                            .alpha(ContentAlpha.medium),
                        onClick = { this() }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "")
                    }
                }
                IconButton(
                    modifier = Modifier
                        .weight(0.1f)
                        .rotate(rotationState)
                        .alpha(ContentAlpha.medium),
                    onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }
            if (isExpanded)
                content()
        }
    }
}