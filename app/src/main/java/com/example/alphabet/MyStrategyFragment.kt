package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.databinding.FragmentMyStrategyBinding
import com.google.android.material.transition.MaterialFadeThrough

class MyStrategyFragment : Fragment(), StrategyListAdapter.OnItemClickListener {
    private lateinit var navController: NavController
    private lateinit var strategyListAdapter: StrategyListAdapter
    private lateinit var databaseViewModel: DatabaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)

        val binding = FragmentMyStrategyBinding.inflate(inflater, container, false)

        val strategyListView = binding.myStrategyList
        strategyListAdapter = StrategyListAdapter(requireContext(), this)
        strategyListView.adapter = strategyListAdapter
        strategyListView.layoutManager = LinearLayoutManager(requireContext())
        strategyListView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        databaseViewModel.readAllStrategy.observe(viewLifecycleOwner) {
            strategyListAdapter.updateList(it)
        }

        setStrategyChipGroupFilter(
            chipGroup = binding.strategyFilterChipGroup,
            db = databaseViewModel,
            adapter = strategyListAdapter
        )

        return binding.root
    }

//    fun filterStrategyType(type: String): List<StrategySchema>? {
//        return databaseViewModel.readAllStrategy.value?.filter { it.strategy.strategyType == type }
//    }

    override fun onItemClick(position: Int, checkBox: CheckBox?) {
        val selectedStrategy = strategyListAdapter.getList()[position]
//        viewModel.customStrategy.value = selectedStrategy.toCustomStrategyInput()
        val action =
            HomeFragmentDirections.actionHomeFragmentToEditStrategyFragment(selectedStrategy)
        navController.navigate(action)
    }
}