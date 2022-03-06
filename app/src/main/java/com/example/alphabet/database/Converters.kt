package com.example.alphabet.database

import androidx.room.TypeConverter
import com.example.alphabet.BacktestResult
import com.example.alphabet.PortfolioInput
import com.example.alphabet.RuleInput
import com.example.alphabet.StrategyInput
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun strategyInputToString(strategyInput: StrategyInput): String {
        return Json.encodeToString(strategyInput)
    }

    @TypeConverter
    fun stringTostrategyInput(string: String): StrategyInput {
        return Json.decodeFromString(string)
    }

    @TypeConverter
    fun backtestResultToString(backtestResult: BacktestResult): String {
        return Json.encodeToString(backtestResult)
    }

    @TypeConverter
    fun stringToBacktestResult(string: String): BacktestResult {
        return Json.decodeFromString(string)
    }

    @TypeConverter
    fun ruleListToString(rules: List<RuleInput>): String {
        return Json.encodeToString(rules)
    }

    @TypeConverter
    fun stringToRuleList(string: String): List<RuleInput> {
        return Json.decodeFromString(string)
    }

    @TypeConverter
    fun portfolioInputListToString(portfolioInputList: List<PortfolioInput>): String {
        return Json.encodeToString(portfolioInputList)
    }

    @TypeConverter
    fun stringToPortfolioInputList(string: String): List<PortfolioInput> {
        return Json.decodeFromString(string)
    }

    @TypeConverter
    fun stringListToString(stringList: List<String>): String {
        return Json.encodeToString(stringList)
    }

    @TypeConverter
    fun stringToStringList(string: String): List<String> {
        return Json.decodeFromString(string)
    }

    @TypeConverter
    fun floatListToString(floatList: List<Float>): String {
        return Json.encodeToString(floatList)
    }

    @TypeConverter
    fun stringToFloatList(string: String): List<Float> {
        return Json.decodeFromString(string)
    }
}