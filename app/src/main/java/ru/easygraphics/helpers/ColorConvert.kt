package ru.easygraphics.helpers

import android.graphics.Color

//конвертация цвета в шестнадцатиричное значение и наоборот
object ColorConvert {
    fun colorToHex(color: Int): String {
        val red = (color shr 16) and 0xff
        val green = (color shr 8) and 0xff
        val blue = color and 0xff
        return String.format("#%02x%02x%02x", red, green, blue)
    }

    fun hexToColor(hex: String): Int {
        return try {
            Color.parseColor(hex)
        } catch (e: Exception) {
            Color.parseColor("#000000")
        }
    }
}