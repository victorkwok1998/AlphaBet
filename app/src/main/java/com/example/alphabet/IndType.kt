package com.example.alphabet


enum class IndType(val id: Int) {
    INDICATOR(R.id.indicator_button),
    VALUE(R.id.specific_value_button),
    PRICE(R.id.price_button),
    BOOL(0);

    companion object {
        private val map = values().associateBy(IndType::id)
        fun fromId(id: Int) = map[id] ?: throw IllegalArgumentException()
    }
}