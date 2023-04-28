package com.example.alphabet

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {
    private val staticDataViewModel: StaticDataViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        setTheme(sp.getString("theme", ""))

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                staticDataViewModel.indicatorStatic =
                    Json.decodeFromString<List<IndicatorStatic>>(getJsonDataFromAsset(applicationContext, "indicatorStatic.json")).sortedBy { it.indName }
                staticDataViewModel.indToParamList =
                    staticDataViewModel.indicatorStatic.associate { it.indName to it.paramName }

                staticDataViewModel.radarChartRange =
                    Json.decodeFromString(getJsonDataFromAsset(applicationContext, "radarChartRange.json"))
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}