package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alphabet.components.*
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.database.StrategySchema
import com.example.alphabet.ui.theme.grayBackground
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CreateStrategyFragment: Fragment() {
    private val viewModel: StrategyViewModel by activityViewModels()
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private val args: CreateStrategyFragmentArgs by navArgs()
    private lateinit var databaseViewModel: DatabaseViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)
        return ComposeView(requireContext()).apply { 
            setContent { 
                val customStrategy = viewModel.customStrategy
                MaterialTheme {
                    CreateStrategyScreen(
                        strategy = customStrategy.value,
                        onOptionSelected = { pos, entryExit ->
                            if (entryExit == EntryExit.ENTRY) {
                                viewModel.copyEntryIndicator(pos)
                            } else {
                                viewModel.copyExitIndicator(pos)
                            }

                            val action = CreateStrategyFragmentDirections.actionCreateStrategyFragmentToCreateRuleFragment(position = pos, entryExit = entryExit)
                            findNavController().navigate(action)
                        },
                        isEdit = args.isEdit
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
                Text("Strategy Name", style = MaterialTheme.typography.h6, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = strategy.strategyName.value,
                    onValueChange = {
                        strategy.strategyName.value = it
                    },
//                    label = { Text("Name") },
                    trailingIcon = {
                        IconButton(onClick = {
                            strategy.strategyName.value = ""
                        }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
//                Spacer(modifier = Modifier.height(10.dp))
//                ThemedTextField(
//                    value = strategy.des.value,
//                    onValueChange = {
//                        strategy.des.value = it
//                    },
//                    label = { Text("Description") },
//                    trailingIcon = {
//                        IconButton(onClick = {
//                            strategy.des.value = ""
//                        }) {
//                            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                )
            }
        }
    }
    
    @Composable
    fun CreateStrategyScreen(
        strategy: CustomStrategyInput,
        onOptionSelected: (Int, EntryExit) -> Unit,
        isEdit: Boolean
    ) {
        val openDialog = remember { mutableStateOf(false)}

        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = { openDialog.value = false },
                confirmButton = {
                    TextButton(onClick = {
                        openDialog.value = false
                        findNavController().popBackStack()
                    }) {
                        Text("Discard")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { openDialog.value = false }) {
                        Text("Cancel")
                    }
                },
                title = {Text("Discard changes?")}
            )
        }

        Scaffold(
            topBar = {
                MyTopAppBar(
                    title = {
                        Text("Edit Strategy")
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            findNavController().popBackStack()
                        }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (!isEdit) {
                                databaseViewModel.addStrategy(StrategySchema(0, strategy.toStrategyInput()))
                                findNavController().popBackStack()
                            } else {
                                //todo
                            }
                        }) {
                            Icon(Icons.Default.Done, null)
                        }
                    }
                )
            },
        ) {
            Column(
                Modifier
                    .background(grayBackground)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                CreateStrategyNameDesCard(strategy)
                Spacer(modifier = Modifier.height(10.dp))
                CreateRuleCard(
                    text = "Entry Rules",
                    rules = strategy.entryRulesInput,
                    onButtonClicked = {
//                        with(viewModel) {
//                            addEmptyEntryRule()
//                        }
                        viewModel.clearEntryRule()
                        val action = CreateStrategyFragmentDirections.actionCreateStrategyFragmentToCreateRuleFragment(position = -1, entryExit=EntryExit.ENTRY)
                        findNavController().navigate(action)
                    },
                    onOptionSelected = { onOptionSelected(it, EntryExit.ENTRY) },
                    entryExit = EntryExit.ENTRY
                )
                Spacer(modifier = Modifier.height(10.dp))
                CreateRuleCard(
                    text = "Exit Rules",
                    rules = strategy.exitRulesInput,
                    onButtonClicked = {
//                        with(viewModel) {
//                            addEmptyExitRule()
//                        }
                        viewModel.clearEntryRule()
                        val action = CreateStrategyFragmentDirections.actionCreateStrategyFragmentToCreateRuleFragment(position=-1, entryExit = EntryExit.EXIT)
                        findNavController().navigate(action)
                    },
                    onOptionSelected = { onOptionSelected(it, EntryExit.EXIT) },
                    entryExit = EntryExit.EXIT
                )
//                Spacer(modifier = Modifier.height(10.dp))
//                if (!isEdit) {
//                    Button(
//                        onClick = {
//                            staticDataViewModel.defaultStrategy.value.add(
//                                viewModel.customStrategy.value.toStrategyInput()
//                            )
//                            findNavController().popBackStack()
//                        },
//                        shape = RoundedCornerShape(50.dp),
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(70.dp)
//                            .padding(10.dp)
//                    ) {
//                        Text("ADD")
//                    }
//                }
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
                "Custom"
            ).toCustomStrategyInput(),
            onOptionSelected = {_, _ ->},
            isEdit = false
        )
    }
}