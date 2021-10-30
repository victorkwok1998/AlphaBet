package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.alphabet.components.*
import com.example.alphabet.ui.theme.grayBackground

class CreateStrategyFragment: Fragment() {
    private val viewModel: StrategyViewModel by activityViewModels()
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply { 
            setContent { 
                val customStrategy = viewModel.customStrategy
                MaterialTheme {
                    CreateStrategyScreen(
                        strategy = customStrategy.value,
                        onOptionSelected = {
                            viewModel.selectedRule.value = it
                            val action = CreateStrategyFragmentDirections.actionCreateStrategyFragmentToCreateRuleFragment()
                            findNavController().navigate(action)
                        },
                        isEdit = viewModel.isEdit.value
                    )
                }
            }
        }
    }

    @Composable
    fun CreateStrategyNameDesCard(strategy: CustomStrategyInput) {
        MyCard(
            Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Name & Description (Optional)", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(10.dp))
                ThemedTextField(
                    value = strategy.strategyName.value,
                    onValueChange = {
                        strategy.strategyName.value = it
                    },
                    label = { Text("Name") },
                    trailingIcon = {
                        IconButton(onClick = {
                            strategy.strategyName.value = ""
                        }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                ThemedTextField(
                    value = strategy.des.value,
                    onValueChange = {
                        strategy.des.value = it
                    },
                    label = { Text("Description") },
                    trailingIcon = {
                        IconButton(onClick = {
                            strategy.des.value = ""
                        }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    
    @Composable
    fun CreateStrategyScreen(
        strategy: CustomStrategyInput,
        onOptionSelected: (RuleInput) -> Unit,
        isEdit: Boolean
    ) {
        Scaffold(
            topBar = {
                MyTopAppBar(
                    title = { Text(strategy.strategyName.value) },
                    navigationIcon = {
                        IconButton(onClick = { findNavController().popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    },
                )
            },
        ) {
            Column(
                Modifier
                    .background(grayBackground)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
//                CreateStrategyNameDesCard(strategy)
//                Spacer(modifier = Modifier.height(10.dp))
                CreateRuleCard(
                    text = "Entry Rules",
                    rules = strategy.entryRulesInput,
                    onButtonClicked = {
                        with(viewModel) {
                            addEmptyEntryRule()
                        }

                        val action = CreateStrategyFragmentDirections.actionCreateStrategyFragmentToCreateRuleFragment()
                        findNavController().navigate(action)
                    },
                    onOptionSelected = onOptionSelected
                )
                Spacer(modifier = Modifier.height(10.dp))
                CreateRuleCard(
                    text = "Exit Rules",
                    rules = strategy.exitRulesInput,
                    onButtonClicked = {
                        with(viewModel) {
                            addEmptyExitRule()
                        }
                        val action = CreateStrategyFragmentDirections.actionCreateStrategyFragmentToCreateRuleFragment()
                        findNavController().navigate(action)
                    },
                    onOptionSelected = onOptionSelected
                )
//                Spacer(modifier = Modifier.height(10.dp))
                if (!isEdit) {
                    Button(
                        onClick = {
                            staticDataViewModel.defaultStrategy.value.add(
                                viewModel.customStrategy.value.toStrategyInput()
                            )
                            findNavController().popBackStack()
                        },
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(10.dp)
                    ) {
                        Text("ADD")
                    }
                }
            }
        }
    }
    
    @Preview
    @Composable
    fun PreviewCreateStrategyScreen() {
        CreateStrategyScreen(
            strategy = StrategyInput(
                "Custom Strategy",
                "",
                mutableListOf(),
                mutableListOf(),
                "",
                ""
            ).toCustomStrategyInput(),
            onOptionSelected = {},
            isEdit = false
        )
    }
}