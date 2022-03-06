package com.example.alphabet.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabet.*
import com.example.alphabet.R
import com.example.alphabet.example.DataExample
import com.example.alphabet.ui.theme.green500
import com.example.alphabet.ui.theme.red500

@Composable
fun RuleCard (
    text: String,
    isButtonVisible: Boolean = true,
    onButtonClicked: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    MyCard(shape = MaterialTheme.shapes.medium,
        modifier = Modifier
        .fillMaxWidth()
        .animateContentSize(
            animationSpec = tween(easing = LinearOutSlowInEasing, durationMillis = 300)
        ),
    ) {
        Column() {
            Spacer(modifier = Modifier.height(5.dp))
            Row(modifier = Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.h6,
                    fontSize = 18.sp,
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
            content()
        }
    }
}

@Composable
fun RuleList(
    rules: MutableList<RuleInput>,
    onOptionSelected: (Int) -> Unit,
    entryExit: EntryExit
) {
    Column {
        rules.forEachIndexed { index, rule ->
            Row(
                Modifier
                    .clickable { onOptionSelected(index) }
                    .padding(vertical = 12.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_line_axis_24),
                    contentDescription = null,
                    tint = when (entryExit) {
                        EntryExit.ENTRY -> green500
                        EntryExit.EXIT -> red500
                    },
                    modifier = Modifier.padding(end = 10.dp),
                )
//                Text("Rule ${index + 1}",
//                    style = MaterialTheme.typography.subtitle1,
//                    modifier = Modifier.weight(0.2f),
//                    color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
//                )
                Text(
                    rule.toString(),
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.weight(0.7f)
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
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun CreateRuleCard(
    text: String,
    rules: MutableList<RuleInput>,
    onButtonClicked: () -> Unit,
    onOptionSelected: (Int) -> Unit,
    entryExit: EntryExit = EntryExit.ENTRY
) {
//    Box {
        RuleCard(
            text = text,
            onButtonClicked = onButtonClicked
        ) {
            RuleList(rules = rules, onOptionSelected = onOptionSelected, entryExit = entryExit)
        }
//    }
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
        onOptionSelected = {},
    )
}