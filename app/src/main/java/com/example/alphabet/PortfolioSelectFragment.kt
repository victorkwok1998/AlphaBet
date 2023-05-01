package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alphabet.database.DatabaseViewModel
import com.example.alphabet.databinding.FragmentPortfolioSelectBinding
import com.example.alphabet.viewmodel.HedgeViewModel

class PortfolioSelectFragment: Fragment(), PortfolioResultAdapter.OnItemClickListener {
    private lateinit var databaseViewModel: DatabaseViewModel
    private val viewModel: HedgeViewModel by navGraphViewModels(R.id.nav_graph_hedge)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databaseViewModel = ViewModelProvider(this).get(DatabaseViewModel::class.java)
        val binding = FragmentPortfolioSelectBinding.inflate(inflater, container, false)
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val adapter = PortfolioResultAdapter(
            context = requireContext(),
            listener = this,
        )
        databaseViewModel.readAllPortfolioResult.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.layoutMyPort.viewEmptyPort.root.isVisible = it.isEmpty()
        }
        binding.layoutMyPort.viewEmptyPort.buttonEmptyList.setOnClickListener {
            val action = PortfolioSelectFragmentDirections.actionGlobalNavGraphPort()
            findNavController().navigate(action)
        }
        binding.layoutMyPort.rvMyPort.adapter = adapter
        binding.layoutMyPort.rvMyPort.layoutManager = LinearLayoutManager(requireContext())
        binding.layoutMyPort.rvMyPort.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        return binding.root
    }

    override fun onItemClick(position: Int) {
        databaseViewModel.readAllPortfolioResult.value?.let {
            viewModel.hedgePort.value = it[position]
            findNavController().popBackStack()
        }
    }
}