package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.databinding.FragmentMyPortfolioBinding

class MyPortfolioFragment: Fragment(), PortfolioResultAdapter.OnItemClickListener {
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)

        val binding = FragmentMyPortfolioBinding.inflate(inflater, container, false)
        val adapter = PortfolioResultAdapter(
            context = requireContext(),
            listener = this,
            onDeleteClicked = { databaseViewModel.deletePortfolioResult(it) },
            onRerunClicked = {},
            onEditClicked = {
                val action = HomeFragmentDirections.actionHomeFragmentToNavGraphPort(it)
                navController.navigate(action)
            }
        )
        binding.rvMyPort.adapter = adapter
        binding.rvMyPort.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyPort.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        databaseViewModel.readAllPortfolioResult.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if (it.isEmpty()) {
                binding.viewEmptyPort.root.visibility = View.VISIBLE
            } else {
                binding.viewEmptyPort.root.visibility = View.GONE
            }
        }
        binding.viewEmptyPort.buttonEmptyList.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToNavGraphPort()
            navController.navigate(action)
        }
        return binding.root
    }

    override fun onItemClick(position: Int) {
        databaseViewModel.readAllPortfolioResult.value?.get(position)?.let {
            val action = HomeFragmentDirections.actionHomeFragmentToPortfolioResultFragment(it)
            navController.navigate(action)
        }
    }
}