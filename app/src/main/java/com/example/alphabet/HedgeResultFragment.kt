package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.alphabet.databinding.FragmentHedgeResultBinding
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.join
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.nield.kotlinstatistics.simpleRegression
import kotlin.math.absoluteValue
import com.example.alphabet.MyApplication.Companion.dec
import com.example.alphabet.MyApplication.Companion.pct
import com.example.alphabet.viewmodel.HedgeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.sqrt

class HedgeResultFragment : Fragment() {
    private val args: HedgeResultFragmentArgs by navArgs()
    private val viewModel: HedgeViewModel by navGraphViewModels(R.id.nav_graph_hedge)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHedgeResultBinding.inflate(inflater, container, false)
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        with(viewModel) {
                val direction = if(regression.slope < 0) "long" else "short"
                binding.textHedge.text = getString(
                    R.string.hedge_text,
                    direction,
                    dec.format(regression.slope.absoluteValue),
                    args.hedgeStock
                )

            val min = regressionData.minOf { it.first }
            val max = regressionData.maxOf { it.first }
            plotLineScatterChart(
                context = requireContext(),
                chart = binding.hedgeChart,
                scatterData = regressionData,
                lineData = listOf(
                    Pair(min, regression.predict(min.toDouble()).toFloat()),
                    Pair(max, regression.predict(max.toDouble()).toFloat()),
                ),
                lineLabel = "Regression Line",
                scatterLabel = "Return Data",
            )
            binding.textXaxisTitle.text = args.hedgeStock
            binding.textYaxisTitle.text = args.port.name
            binding.textBeta.text = dec.format(regression.slope)
            binding.textAlpha.text = pct.format(regression.intercept * 252)
            binding.textCorrelation.text = dec.format(regression.r)
        }
        return binding.root

    }
}