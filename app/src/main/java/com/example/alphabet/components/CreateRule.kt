package com.example.alphabet.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphabet.Cond
import com.example.alphabet.R

@ExperimentalComposeUiApi
@Composable
fun CreateRule(
    primaryIndicator: String,
    primaryParamNames: List<String>,
    primaryParamValues: MutableList<String>,
    primaryOnClick: () -> Unit,
    primaryOnValueChange: (Int, String) -> Unit,
    secondIndicator: String,
    secondParamNames: List<String>,
    secondParamValues: MutableList<String>,
    secondOnClick: () -> Unit,
    secondOnValueChange: (Int, String) -> Unit,
    condOnValueChange: (String) -> Unit
) {
    var cond by remember { mutableStateOf(Cond.CROSS_UP) }
    MyCard(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Step 1: Select Primary Indicator", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.padding(10.dp))
            IndicatorSelector(
                value = primaryIndicator,
                paramNames = primaryParamNames,
                paramValues = primaryParamValues,
                label = { Text("Primary Indicator") },
                onClick = primaryOnClick,
                onValueChange = primaryOnValueChange
            )
            Divider(modifier = Modifier.padding(vertical = 20.dp))
            Text("Step 2: Select Condition", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.padding(10.dp))
            DropDownTextField(
                value = cond.value,
                possibleValues = Cond.values().map { it.value },
                onChange = {
                    cond = Cond.fromValue(it)
                    condOnValueChange(it)
                },
            )
            Divider(modifier = Modifier.padding(vertical = 20.dp))
            Text("Step 3: Select Secondary Indicator", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.padding(10.dp))
            IndicatorSelector(
                value = secondIndicator,
                paramNames = secondParamNames,
                paramValues = secondParamValues,
                label = { Text("Secondary Indicator") },
                onClick = secondOnClick,
                onValueChange = secondOnValueChange
            )
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun IndicatorSelector(
    value: String,
    paramNames: List<String>,
    paramValues: MutableList<String>,
    label: (@Composable () -> Unit)? = null,
    onValueChange: (Int, String) -> Unit,
    onClick: () -> Unit
) {
    Column {
        ClickableTextField(
            value = value,
            label = label,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clickable(onClick = onClick),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_candlestick_chart_24),
                    contentDescription = null
                )
            },
            trailingIcon = { Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null)}
        )
        if (paramNames.isNotEmpty()) {
            Text(
                "Parameters",
                style = MaterialTheme.typography.subtitle1,
//                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
            )
            ParameterTextFieldGroup(paramNames, paramValues, onValueChange)
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun ColumnScope.ParameterTextFieldGroup(
    paramNames: List<String>,
    paramValues: MutableList<String>,
    onValueChange: (Int, String) -> Unit
) {
    paramNames
        .zip(paramValues)
        .forEachIndexed { index, (paramName, paramValue) ->
        var value by remember { mutableStateOf(paramValue) }
        MyTextField(
            value = value,
            onValueChange = {
                value = it
                paramValues[index] = it
//                onValueChange(index, it)
            },
            label = { Text(paramName) },
            modifier = Modifier.padding(vertical = 10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.padding(start = 15.dp, top = 5.dp, bottom = 5.dp)
//        ) {
//            Text(
//                paramName,
//                style = MaterialTheme.typography.subtitle1,
//                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
//                modifier = Modifier.weight(0.3f)
//            )
//            MyBasicTextField(
//                value = value,
//                onValueChange = {
//                    value = it
//                    onValueChange(index, it)
//                },
//                modifier = Modifier.weight(0.7f))
//        }
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun PreviewCreateRule() {
    CreateRule(
        primaryIndicator = "RSI",
        primaryParamNames = listOf("Bar Count"),
        primaryParamValues = mutableListOf(""),
        primaryOnClick = {},
        primaryOnValueChange = { _, _ ->},
        secondIndicator = "30",
        secondOnClick = {},
        secondOnValueChange = { _, _ ->},
        secondParamNames = listOf(),
        secondParamValues = mutableListOf(),
        condOnValueChange = {}
    )
}