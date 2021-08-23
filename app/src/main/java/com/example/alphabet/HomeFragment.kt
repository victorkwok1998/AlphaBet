package com.example.alphabet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.alphabet.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class HomeFragment: Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        lifecycleScope.launch() {
            binding.loadingLayout.visibility = View.VISIBLE
            binding.homeLayout.visibility = View.GONE
            if (staticDataViewModel.indToParamList.value == null) {
                // Read static data
                withContext(Dispatchers.IO) {
                    staticDataViewModel.indToParamList.postValue(
                        Json.decodeFromString<Map<String, List<String>>>(getJsonDataFromAsset(requireContext(), "indToParamList.json"))
                    )
                    staticDataViewModel.defaultStrategy.postValue(
                        Json.decodeFromString<Map<StrategyName, StrategyInput>>(getJsonDataFromAsset(requireContext(), "defaultStrategy.json"))
                    )
                    staticDataViewModel.radarChartRange.postValue(
                        Json.decodeFromString<Map<String, List<Float>>>(getJsonDataFromAsset(requireContext(), "radarChartRange.json"))
                    )
//                    staticDataViewModel.myStrategy.postValue(
//                        Json.decodeFromString<BacktestResult>(getJsonDataFromAsset(requireContext(), "myStrategy.json"))
//                    )
                }
            }
            withContext(Dispatchers.IO) {
                // Copy to internal storage from asset if not exist
                val file = File(requireContext().filesDir, "myStrategy.json")
                if (!file.exists()) {
                    file.createNewFile()
                    getJsonDataFromAsset(requireContext(), file.name)
                        .apply {
                            file.writeText(this)
                            staticDataViewModel.myStrategy.postValue(Json.decodeFromString<BacktestResult>(this))
                        }
                } else {
                    staticDataViewModel.myStrategy.postValue(Json.decodeFromString<BacktestResult>(
                        readFile(file)))
                }
            }
            binding.loadingLayout.visibility = View.GONE
            binding.homeLayout.visibility = View.VISIBLE
            //plot
            with(staticDataViewModel.myStrategy.value!!) {
                plotEquityCurve(
                    binding.stratChart,
                    this.date.map { MyApplication.sdfISO.parse(it)!! },
                    this.cashFlow,
                    this.name + " Strategy",
                    requireContext()
                )
                binding.symbol.text = this.symbol
                val pnl = (cashFlow[cashFlow.size - 1] - 1) * 100
                var pnlString = String.format("%.2f", pnl) + "%"
                if (pnl > 0) {
                    pnlString = "+" + pnlString
                    binding.pnlText.setBackgroundResource(R.color.green)
                } else if (pnl < 0) {
                    binding.pnlText.setBackgroundResource(R.color.red)
                }
                binding.pnlText.text = pnlString
                binding.dateRange.text = requireContext().getString(
                    R.string.date_range,
                    isoToDisplay(this.date[0]),
                    isoToDisplay(this.date.last())
                )
            }
        }

        binding.createStratButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSelectSymbolFragment()
            findNavController().navigate(action)
        }
        return binding.root
    }

    private fun isoToDisplay(dateString: String): String {
        return dateString
            .run { MyApplication.sdfISO.parse(this) }
            .run { MyApplication.sdfLong.format(this) }
    }
}