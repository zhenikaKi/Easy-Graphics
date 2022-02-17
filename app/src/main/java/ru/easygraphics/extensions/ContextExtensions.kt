package ru.easygraphics.extensions

import android.content.Context
import android.widget.Toast

fun Context.toast(string: String?) {
    Toast.makeText(this, string, Toast.LENGTH_LONG).show()
}