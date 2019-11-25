package es.iessaladillo.pedrojoya.pr04.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

// NO TOCAR

// Clase que representa un evento, cuyo contenido sólo puede ser
// obtenido una única vez.
open class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

}

// Permite observar directamente el contenido de un Event.
inline fun <T> LiveData<Event<T>>.observeEvent(
    owner: LifecycleOwner, crossinline onEventUnhandledContent: (T) -> Unit) {
    observe(owner, Observer { it.getContentIfNotHandled()?.let(onEventUnhandledContent) })
}