package ru.skillbranch.devintensive.extensions

import android.R.id.content
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.app.Activity as Activity

fun Activity.hideKeyboard() {
    val view = this.currentFocus
    view?.let { v ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
    }
}

fun Activity.isKeyboardOpen(): Boolean {
        val rootView = findViewById<View>(content)
        val softKeyboardHeight = 100
        val rect = Rect()

        rootView.getWindowVisibleDisplayFrame(rect)

        val dm = rootView.resources.displayMetrics
        val heightDiff = rootView.bottom - rect.bottom
        return heightDiff > softKeyboardHeight * dm.density
}

fun Activity.isKeyboardClosed(): Boolean = !this.isKeyboardOpen()
