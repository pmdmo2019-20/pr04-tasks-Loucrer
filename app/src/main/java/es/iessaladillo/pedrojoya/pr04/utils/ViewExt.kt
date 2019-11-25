package es.iessaladillo.pedrojoya.pr04.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

// NO TOCAR

// Hace que la vista esté invisible a no ser que se cumpla la condición,
// en cuyo caso estará visible.
fun View.invisibleUnless(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.INVISIBLE
}

// Oculta el teclado virtual
fun View.hideKeyboard(): Boolean {
    try {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) { }
    return false
}
