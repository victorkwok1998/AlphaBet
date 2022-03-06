package com.example.alphabet.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.alphabet.BacktestResult
import com.example.alphabet.PortfolioInput
import com.example.alphabet.StrategyInput


@Entity(tableName = "backtest_result")
data class BacktestResultSchema(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val backtestResult: BacktestResult
)

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

@Entity(tableName = "strategy")
data class StrategySchema(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @Embedded
    val strategy: StrategyInput
)

@Entity(tableName = "portfolio_result")
data class PortfolioResultSchema(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val portfolioInputList: List<PortfolioInput>,
    val date: List<String>,
    val nav: List<Float>
)