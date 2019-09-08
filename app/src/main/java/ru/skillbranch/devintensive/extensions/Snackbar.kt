package ru.skillbranch.devintensive.extensions

import android.graphics.Color
import android.widget.TextView
import androidx.annotation.ColorInt
import com.google.android.material.snackbar.Snackbar

fun Snackbar.withColors(@ColorInt background: Int, @ColorInt foreground: Int = Color.WHITE) : Snackbar {
    view.setBackgroundColor(background)
    view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).setTextColor(foreground)
    return this
}