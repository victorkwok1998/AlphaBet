package com.example.alphabet.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphabet.BacktestInput
import com.example.alphabet.R
import com.example.alphabet.RuleInput
import com.example.alphabet.example.DataExample

@Composable
fun RuleCard (
    text: String,
    isButtonVisible: Boolean = true,
    onButtonClicked: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    MyCard(modifier = Modifier
        .fillMaxWidth()
        .animateContentSize(
            animationSpec = tween(easing = LinearOutSlowInEasing, durationMillis = 300)
        ),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.weight(0.8f)
                )
                if (isButtonVisible){
                    IconButton(
                        onClick = onButtonClicked,
                        modifier = Modifier.weight(0.2f)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    }
                }
            }
//            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
fun RuleList(
    rules: MutableList<RuleInput>,
    onOptionSelected: (RuleInput) -> Unit,
) {
    Column {
        rules.forEachIndexed { index, rule ->
            Row(
                Modifier
                    .clickable { onOptionSelected(rule) }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Rule ${index + 1}",
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.weight(0.2f),
                    color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                )
                Text(
                    rule.toString(),
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.weight(0.6f)
                )
                IconButton(
                    onClick = { rules.removeAt(index) },
                    modifier = Modifier.weight(0.2f)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_cancel_24),
                        contentDescription = null,
                        tint = Color.LightGray
                    )
                }
            }
            if (index < rules.lastIndex)
                Divider()
        }
        if (rules.isEmpty()) {
            Text(
                "Click \"+\" to create first rule",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                modifier = Modifier.padding(vertical = 10.dp)
            )
        }
    }
}

@Composable
fun CreateRuleCard(
    text: String,
    rules: MutableList<RuleInput>,
    onButtonClicked: () -> Unit,
    onOptionSelected: (RuleInput) -> Unit,
) {
    Box {
        RuleCard(
            text = text,
            onButtonClicked = onButtonClicked
        ) {
            RuleList(rules = rules, onOptionSelected = onOptionSelected)
        }
    }
//    RuleCard(text = text) {
//        RuleList(rules = rules)
////        Spacer(modifier = Modifier.padding(10.dp))
////        Button(
////            onClick = onButtonClicked,
////            shape = RoundedCornerShape(20.dp),
////            modifier = Modifier
////                .fillMaxWidth()
////                .height(40.dp)) {
////            Text("Add Rule")
////        }
//    }
}

@Preview
@Composable
fun PreviewCreateRuleCard() {
    val rules = remember{mutableStateListOf(DataExample().entry)}
    CreateRuleCard(
        text = "Entry Rules",
        rules = rules,
        onButtonClicked = {},
        onOptionSelected = {}
    )
}