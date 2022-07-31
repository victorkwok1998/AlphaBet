package com.example.alphabet

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alphabet.databinding.FragmentShareBacktestBinding
import com.example.alphabet.util.Constants.Companion.pct
import java.io.File
import java.io.FileOutputStream

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
            textShareSymbol.text = args.backtestResult.backtestInput.stock.symbol
            textShareStrategy.text = args.backtestResult.backtestInput.strategyInput.strategyName
            textSharePeriod.text = getBacktestPeriodString(args.backtestResult, requireContext())

            buttonShareBacktestScreen.setOnClickListener {
                val bitmap = getBitmapFromView(layoutShareBacktest)
                val imagesFolder = File(requireContext().cacheDir, "images")
                imagesFolder.mkdirs()
                val file = File(imagesFolder, "backtest_result.png")
                val stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
                stream.flush()
                stream.close()
                val uri = FileProvider.getUriForFile(requireContext(), requireContext().applicationContext.packageName + ".provider",  file)

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "image/*"
                    addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                }
                startActivity(shareIntent)
            }
        }
        return binding.root
    }
}