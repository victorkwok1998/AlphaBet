package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alphabet.databinding.FragmentShareBacktestBinding
import com.example.alphabet.ui.shareImage
import com.example.alphabet.util.Constants.Companion.pct

class ShareBacktestFragment: Fragment() {
    private val args: ShareBacktestFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentShareBacktestBinding.inflate(inflater, null, false)
        with(binding) {
            topAppBar.setNavigationOnClickListener { findNavController().popBackStack() }

            val shareIcon: Int?
            val slogan: String?
            when {
                args.backtestResult.getMetrics().pnlPct> 0 -> {
                    shareIcon = R.drawable.ic_outline_rocket_launch_24
                    slogan = getString(R.string.up_slogan)
                    textShareReturn.setTextColor(requireContext().getColor(R.color.green))
                }
                else -> {
                    shareIcon = R.drawable.ic_outline_diamond_24
                    slogan = getString(R.string.down_slogan)
                    textShareReturn.setTextColor(requireContext().getColor(R.color.red))
                }
            }
            imageShareIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), shareIcon))
            textShareSlogan.text = slogan
            textShareReturn.text = pct.format(args.backtestResult.getMetrics().pnlPct)
            textShareStrategy.text = args.backtestResult.backtestInput.strategyInput.strategy.strategyName
            textSharePeriod.text = getBacktestPeriodString(args.backtestResult, requireContext())
            val stock = args.backtestResult.backtestInput.stock
            textShareStockName.text = getString(R.string.stock_display, stock.longname, stock.symbol)

            buttonShareBacktestScreen.setOnClickListener {
                val bitmap = getBitmapFromView(layoutShareBacktest)
                shareImage(requireContext(), bitmap)
            }
        }
        return binding.root
    }
}