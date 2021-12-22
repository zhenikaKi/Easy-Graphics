package ru.easygraphics.helpers

import com.github.mikephil.charting.formatter.ValueFormatter

/** Преобразователь для значений по оси X */
class XValueFormatter(private val names: List<String>?): ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        return if (names == null || index >= names.size) value.toString() else names[index]
    }
}