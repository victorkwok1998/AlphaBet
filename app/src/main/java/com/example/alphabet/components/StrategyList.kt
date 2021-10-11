package com.example.alphabet.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabet.StrategyInput

@Composable
fun StrategyList(
    strategies: List<StrategyInput>,
    onOptionSelected: (Int) -> Unit,
) {
    Column {
        strategies.forEachIndexed { index, strategyInput ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        onOptionSelected(index)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                StrategyRow(strategyInput = strategyInput)
            }
            if (index < strategies.lastIndex)
                Divider(thickness = 1.dp)
        }
    }
}

@Composable
fun StrategyRow(strategyInput: StrategyInput) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(targetValue = if(isExpanded) 180f else 0f)

    Box(Modifier.fillMaxWidth().padding(vertical = 15.dp)) {
        Column() {
            Text(
                text = strategyInput.strategyName,
                style = MaterialTheme.typography.body1,
                fontSize = 18.sp
//                            fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            Text(
                strategyInput.des,
                style = MaterialTheme.typography.body1,
                maxLines = if(isExpanded) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(
                    bottom = if (isExpanded) 20.dp else 0.dp,
                    end = 20.dp
                ),
            )
        }
        IconButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier
                    .rotate(rotationState)
                    .background(MaterialTheme.colors.background.copy(alpha = ContentAlpha.medium))
            )
        }
    }
}
