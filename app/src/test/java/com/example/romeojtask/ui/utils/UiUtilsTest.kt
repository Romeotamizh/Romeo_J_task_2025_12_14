package com.example.romeojtask.ui.utils

import android.content.Context
import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.romeojtask.R
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28]) // Configure for a specific SDK version
class UiUtilsTest {

    private lateinit var context: Context
    private lateinit var textView: TextView

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        context.setTheme(R.style.AppTheme) // Use the actual app theme
        textView = TextView(context)
    }

    @Test
    fun `toIndianCurrency formats positive value correctly`() {
        val result = 123456.78.toIndianCurrency()
        assertThat(result).startsWith("₹")
        assertThat(result).endsWith(".78")
    }

    @Test
    fun `toIndianCurrency formats negative value correctly`() {
        val result = (-9876.5).toIndianCurrency()
        assertThat(result).startsWith("-₹")
        assertThat(result).endsWith(".50")
    }

    @Test
    fun `setPnlTextColor sets green for positive value`() {
        textView.setPnlTextColor(100.0)
        val greenColor = ContextCompat.getColor(context, R.color.green)
        assertThat(textView.currentTextColor).isEqualTo(greenColor)
    }

    @Test
    fun `setPnlTextColor sets red for negative value`() {
        textView.setPnlTextColor(-50.0)
        val redColor = ContextCompat.getColor(context, R.color.red)
        assertThat(textView.currentTextColor).isEqualTo(redColor)
    }

    @Test
    fun `setPnlTextColor sets default color for zero`() {
        // Act
        textView.setPnlTextColor(0.0)

        // Assert
        val expectedColor = resolveThemeColor(R.attr.textColorSubText)
        assertThat(textView.currentTextColor).isEqualTo(expectedColor)
    }

    private fun resolveThemeColor(attrId: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrId, typedValue, true)
        return typedValue.data
    }
}