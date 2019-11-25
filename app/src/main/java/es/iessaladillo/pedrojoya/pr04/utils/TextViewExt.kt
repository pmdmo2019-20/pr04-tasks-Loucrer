package es.iessaladillo.pedrojoya.pr04.utils

import android.graphics.Paint
import android.widget.TextView

// NO TOCAR

// Tacha o no el texto de un TextView
fun TextView.strikeThrough(strike: Boolean) {
    paintFlags = if (strike) {
        paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}
