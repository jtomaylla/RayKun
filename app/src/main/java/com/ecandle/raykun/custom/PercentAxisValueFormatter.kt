package com.ecandle.raykun.custom

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.DecimalFormat

class PercentAxisValueFormatter : IAxisValueFormatter {

    private val mFormat: DecimalFormat

    init {
        mFormat = DecimalFormat("##0")
    }

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        return mFormat.format(value.toDouble()) + "%"
    }
}