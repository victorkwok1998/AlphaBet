package com.example.alphabet

enum class Cond(val id: Int) {
    CROSS_UP(R.id.crossup_button),
    CROSS_DOWN(R.id.crossdown_button),
    OVER(R.id.over_button),
    UNDER(R.id.under_button);

    companion object {
        private val map = values().associateBy(Cond::id)
        fun fromId(id: Int) = map[id] ?: throw IllegalArgumentException()
    }
}