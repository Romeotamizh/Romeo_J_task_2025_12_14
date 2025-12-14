package com.example.romeojtask.ui.utils

import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.romeojtask.R
import java.text.NumberFormat
import java.util.Locale

private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

fun Double.toIndianCurrency(): String {
    return currencyFormat.format(this)
}

fun TextView.setPnlTextColor(value: Double) {
    val color = when {
        value > 0 -> ContextCompat.getColor(context, R.color.green)
        value < 0 -> ContextCompat.getColor(context, R.color.red)
        else -> {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.textColorSubText, typedValue, true)
            typedValue.data
        }
    }
    setTextColor(color)
}
