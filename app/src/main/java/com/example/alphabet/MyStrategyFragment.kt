package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.components.MyCard
import com.example.alphabet.components.StrategyList
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.database.StrategySchema
import com.example.alphabet.databinding.FragmentMyStrategyBinding
import com.example.alphabet.ui.theme.grayBackground

class MyStrategyFragment: Fragment(), StrategyListAdapter.OnItemClickListener {
    private val viewModel: StrategyViewModel by activityViewModels()
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var strategyListAdapter: StrategyListAdapter
    private lateinit var databaseViewModel: DatabaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)

        val binding = FragmentMyStrategyBinding.inflate(inflater, container, false)

        val strategyListView = binding.myStrategyList
        strategyListAdapter = StrategyListAdapter(this)
        strategyListView.adapter = strategyListAdapter
        strategyListView.layoutManager = LinearLayoutManager(requireContext())
        strategyListView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        databaseViewModel.readAllStrategy.observe(viewLifecycleOwner) {
            strategyListAdapter.updateList(it)
        }

        binding.strategyFilterChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val tmp = when(checkedId) {
                R.id.all_chip -> databaseViewModel.readAllStrategy.value
                R.id.momentum_chip -> filterStrategyType("Momentum")
                R.id.reversal_chip -> filterStrategyType("Reversal")
                R.id.passive_chip -> filterStrategyType("Passive")
                R.id.custom_chip -> filterStrategyType("Custom")
                else -> listOf()
            }
            tmp?.let { strategyListAdapter.updateList(it) }
        }

        return binding.root

//        return ComposeView(requireContext()).apply {
//            setContent {
//                MaterialTheme() {
//                    MyStrategyScreen(staticDataViewModel.defaultStrategy.value)
//                }
//            }
//        }
    }

    fun filterStrategyType(type: String): List<StrategySchema>? {
        return databaseViewModel.readAllStrategy.value?.filter { it.strategy.strategyType == type }
    }

    override fun onItemClick(position: Int) {
        val selectedStrategy = strategyListAdapter.getList()[position].strategy
        viewModel.customStrategy.value = selectedStrategy.toCustomStrategyInput()
        val action = HomeFragmentDirections.actionHomeFragmentToCreateStrategyFragment(true)
        navController.navigate(action)
    }

    @Composable
    fun MyStrategyScreen(strategies: List<StrategyInput>) {
        Column(
            Modifier
                .background(grayBackground)
        ) {
            MyCard(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            {
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())) {
                    StrategyList(
                        strategies = strategies,
                        onOptionSelected = {
                            viewModel.customStrategy.value = it.toCustomStrategyInput()
                            val action = HomeFragmentDirections.actionHomeFragmentToCreateStrategyFragment(true)
                            navController.navigate(action)
                        }
                    )
                    // Extra space for scroll above fab
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}