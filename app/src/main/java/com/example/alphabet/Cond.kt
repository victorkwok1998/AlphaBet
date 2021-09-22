package com.example.alphabet

enum class Cond(val value: String) {
    CROSS_UP("CROSS UP"),
    CROSS_DOWN("CROSS DOWN"),
    OVER("OVER"),
    UNDER("UNDER");

    companion object {
        private val map = values().associateBy(Cond::value)
        fun fromValue(condName: String) = map[condName] ?: throw IllegalArgumentException()
    }
}