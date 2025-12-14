package com.example.romeojtask.ui.utils

import java.text.NumberFormat
import java.util.Locale

object FormattingUtils {
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    fun formatToIndianCurrency(value: Double): String {
        return currencyFormat.format(value)
    }
}