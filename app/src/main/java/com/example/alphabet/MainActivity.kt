package com.example.alphabet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import org.ta4j.core.BaseBarSeries
import yahoofinance.YahooFinance
import org.ta4j.core.rules.CrossedDownIndicatorRule
import yahoofinance.histquotes.Interval
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}