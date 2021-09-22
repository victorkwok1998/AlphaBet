package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.alphabet.components.DropDownTextField
import com.example.alphabet.components.MyTopAppBar
import com.example.alphabet.ui.theme.grayBackground

//import com.example.alphabet.components.DropDownTextField

class EditStrategyFragment : Fragment() {
    private val viewModel: StrategyViewModel by activityViewModels()
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                EditStrategyScreen()
            }
        }
    }


    @Composable
    fun EditStrategyScreen() {
        val selectedStrategy =
            staticDataViewModel.defaultStrategy.value[viewModel.selectToEditStrategy.value]
        Scaffold(
            topBar = { MyTopAppBar(title = { Text("Edit") },
                navigationIcon = {
                    IconButton(onClick = {
                        findNavController().popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    }
                }) },
//            bottomBar = {
//                BottomAppBar {
//                    BottomBarItem(icon = R.drawable.ic_baseline_restore_24, text = "Reset") {
//                        //TODO: reset
//                    }
//                    BottomBarItem(icon = R.drawable.ic_baseline_check_24, text = "Save", onClick = {
//                        //TODO: update staticDataViewModel.defaultStrategy
//                        lifecycleScope.launch {
//                            withContext(Dispatchers.IO) {
//                                File(requireContext().filesDir, "defaultStrategy.json")
//                                    .writeText(
//                                        Json.encodeToString(staticDataViewModel.defaultStrategy.value.toMap())
//                                    )
//                            }
//                            val action =
//                                EditStrategyFragmentDirections.actionEditStrategyFragmentToSelectStrategyFragment()
//                            findNavController().navigate(action)
//                        }
//                    })
//                }
//            },
            content = {
                var stopGain by remember { mutableStateOf(selectedStrategy.second.stopGain)}
                var stopLoss by remember { mutableStateOf(selectedStrategy.second.stopLoss)}

                Column(
                    Modifier
                        .fillMaxSize()
                        .background(grayBackground)
                        .verticalScroll(rememberScrollState())
                ) {
//                    Spacer(modifier = Modifier.height(10.dp))
                    StrategyDescCard(selectedStrategy.second)
                    Spacer(modifier = Modifier.height(10.dp))
                    RuleCard(text = "Entry Rules") {
                        RuleInputList(rules = selectedStrategy.second.entryRulesInput)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    RuleCard(text = "Exit Rules") {
                        RuleInputList(rules = selectedStrategy.second.exitRulesInput)
                        Divider(Modifier.padding(vertical = 10.dp))
                        ParameterRow(
                            rowName = "Stop Gain (%)",
                            value = stopGain,
                            onValueChange = { stopGain = it })
                        Spacer(Modifier.height(10.dp))
                        ParameterRow(
                            rowName = "Stop Loss (%)",
                            value = stopLoss,
                            onValueChange = { stopLoss = it })
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        )
    }

    @Composable
    fun IndicatorInputRow(label: String, indInput: IndicatorInput) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                label,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.weight(0.3f)
            )
            if (indInput.indType == IndType.VALUE) {
                var text by remember { mutableStateOf(indInput.indName) }
                TextField(
                    value = text,
                    onValueChange = {
                        indInput.indName = it
                        text = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.7f)
                )
            } else {
                Text(
                    indInput.indName,
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(vertical = 10.dp),
                    style = MaterialTheme.typography.subtitle1
                )
            }

        }
        if (indInput.indType == IndType.INDICATOR) {
            val texts = indInput.indParamList.map { mutableStateOf(it) }
            Spacer(modifier = Modifier.height(10.dp))
            staticDataViewModel.indToParamList.value[indInput.indName]!!
                .zip(indInput.indParamList)
                .forEachIndexed { index, (paramName, _) ->
                    ParameterRow(rowName = paramName, value = texts[index].value, onValueChange = {
                        texts[index].value = it
                        indInput.indParamList[index] = it
                    })
                }
        }
    }

    @Composable
    fun ParameterRow (rowName: String, value: String, onValueChange: (String) -> Unit ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                rowName,
                modifier = Modifier.weight(0.3f),
                style = MaterialTheme.typography.subtitle1,
            )
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(0.7f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }

    @Composable
    fun RuleCard (text: String, content: @Composable () -> Unit) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(easing = LinearOutSlowInEasing, durationMillis = 300)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.h6
                )
                Spacer(modifier = Modifier.height(20.dp))
                content()
            }
        }
    }

    @Composable
    fun RuleInputList(rules: List<RuleInput>) {
        Column() {
            rules.forEachIndexed { index, rule ->
                var isExpanded by remember { mutableStateOf(false) }
                val rotationState by animateFloatAsState(targetValue = if(isExpanded) 180f else 0f)

                Row(Modifier.clickable {
                    isExpanded = !isExpanded
                },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Rule ${index + 1}", 
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.weight(0.3f)
                    )
                    Text(
                        rule.toString(),
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.weight(0.5f)
                    )
                    IconButton(
                        modifier = Modifier
                            .weight(0.2f)
                            .rotate(rotationState)
                            .alpha(ContentAlpha.medium),
                        onClick = { isExpanded = !isExpanded }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "")
                    }
                }
                if(isExpanded && rule.indInput1.indType != IndType.BOOL) {
                    Column(Modifier
                        .padding(horizontal = 10.dp)) {
                        IndicatorInputRow("Primary Indicator", rule.indInput1)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Condition",
                                style = MaterialTheme.typography.subtitle1,
                                modifier = Modifier.weight(0.3f)
                            )
                            DropDownTextField(
                                value = rule.condName.value,
                                possibleValues = Cond.values().map { it.value },
                                onChange = { rule.condName = Cond.fromValue(it) },
                                modifier = Modifier.weight(0.7f)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        IndicatorInputRow("Secondary Indicator", rule.indInput2)
                    }
                }
                if (index < rules.lastIndex)
                    Divider(modifier = Modifier.padding(vertical = 10.dp))
            }
        }
    }

    @Composable
    fun StrategyDescCard(strategy: StrategyInput) {
        var isExpanded by remember { mutableStateOf(false) }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                Modifier
                    .padding(20.dp)
                    .clickable { isExpanded = !isExpanded }
                    .animateContentSize()
            ) {
                Text("Strategy Description", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    strategy.des,
                    style = MaterialTheme.typography.subtitle1,
                    maxLines = if(isExpanded) Int.MAX_VALUE else 2,
                    modifier = Modifier
                )
                if(!isExpanded) {
                    Text(
                        "...",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
//                        modifier = Modifier.background(color = Color.LightGray)
                    )
                }
            }
        }

    }
}