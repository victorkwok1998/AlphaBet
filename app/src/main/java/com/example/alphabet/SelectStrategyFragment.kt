package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.components.MyCard
import com.example.alphabet.components.MyTopAppBar
import com.example.alphabet.components.SearchBar
import com.example.alphabet.components.StrategyList
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.databinding.FragmentSelectStrategyBinding
import com.example.alphabet.example.DataExample
import com.example.alphabet.ui.theme.grayBackground

class SelectStrategyFragment: Fragment(), StrategyListAdapter.OnItemClickListener {
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private val viewModel: StrategyViewModel by activityViewModels()
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var strategyListAdapter: StrategyListAdapter
    private val args: SelectStrategyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)
        val binding = FragmentSelectStrategyBinding.inflate(inflater, container, false)
        strategyListAdapter = StrategyListAdapter(this)

        binding.topAppBar.setNavigationOnClickListener { findNavController().popBackStack() }

        val strategyListView = binding.strategyList.myStrategyList
        strategyListView.adapter = strategyListAdapter
        strategyListView.layoutManager = LinearLayoutManager(requireContext())
        strategyListView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        databaseViewModel.readAllStrategy.observe(viewLifecycleOwner) {
            strategyListAdapter.updateList(it)
        }
        return binding.root

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    SelectStrategy(
                        staticDataViewModel.defaultStrategy.value,
                    )
                }
            }
        }
    }

    override fun onItemClick(position: Int) {
        val item = strategyListAdapter.getList()[position].strategy
        if (args.position == -1) {
            viewModel.strategyList.value = viewModel.strategyList.value?.plus(item)
        } else {
//            viewModel.strategyList.value!![args.position] = item
        }
//        viewModel.symbolStrategyList[args.position].strategyInput = item
        findNavController().popBackStack()
    }

    @Composable
    fun SelectStrategy(strategies: List<StrategyInput>) {
        var searchText by remember { mutableStateOf("")}
        val filteredStrategies = if (searchText.isEmpty()) strategies else
            strategies.filter { it.strategyName.lowercase().contains(searchText.lowercase()) }

        Scaffold(
            topBar = {
                MyTopAppBar(
                    title = {
                    SearchBar(value = searchText, onValueChange = { searchText = it })
                },
                    navigationIcon = {
                        IconButton(onClick = { findNavController().popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                )
            },
            content = { paddingValues ->
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(grayBackground)) {
                    MyCard(
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            Modifier
//                            .padding(paddingValues)
                                .verticalScroll(rememberScrollState())
                        ) {
                            StrategyList(
                                filteredStrategies,
                                onOptionSelected = {
                                    with(viewModel) {
                                        symbolStrategyList[inputToSelectStrategy.value].strategyInput = it.copy()
                                    }
                                    findNavController().popBackStack()
                                },
//                        onIconClick = { index ->
////                            viewModel.selectToEditStrategy.value = index
//                            viewModel.symbolStrategyList[viewModel.inputToSelectStrategy.value].strategyInput = staticDataViewModel.defaultStrategy.value[index].copy()
//                            val action = SelectStrategyFragmentDirections.actionSelectStrategyFragmentToEditStrategyFragment()
//                            findNavController().navigate(action)
//                        }
                            )
                        }
                    }
                }

            }
        )


    }

    @Preview
    @Composable
    fun PreviewStrategyRow() {
        val strategies =  listOf(
            DataExample().strategyInput
        )
        SelectStrategy(strategies)
    }
}