package com.example.alphabet.database

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.alphabet.BacktestResult
import com.example.alphabet.PortfolioInput
import com.example.alphabet.StrategyInput
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "backtest_result")
data class BacktestResultSchema(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val backtestResult: BacktestResult
): Parcelable

//@Entity(tableName = "backtest_result_cash_flow", primaryKeys = ["backtestResultId", "date"])
//data class BacktestResultCashFlowSchema(
//    val backtestResultId: Long,
//    val date: String,
//    val cashFlow: Float
//)
//
//data class BacktestResultWithCashFlow(
//    @Embedded val backtestResult: BacktestResultSchema,
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "backtestResultId"
//    )
//    val cashflow: List<BacktestResultCashFlowSchema>
//)

@Parcelize
@Entity(tableName = "strategy")
data class StrategySchema(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @Embedded
    val strategy: StrategyInput
): Parcelable

@Parcelize
@Entity(tableName = "portfolio_result")
data class PortfolioResultSchema(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var name: String,
    val portfolioInputList: List<PortfolioInput>,
    val date: List<String>,
    val nav: List<Float>
) : Parcelable