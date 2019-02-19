package com.ripe.android.base

import kotlin.reflect.KFunction

open class Observable {
    val callbacks: MutableMap<String, MutableList<KFunction<Unit>>> = HashMap()

    fun addCallback(event: String, callback: KFunction<Unit>): KFunction<Unit> {
        val callbacks = this.callbacks[event] ?: ArrayList()
        callbacks.add(callback)
        this.callbacks[event] = callbacks
        return callback
    }

    fun removeCallback(event: String, callback: (KFunction<Unit>)?) {
        val callbacks = this.callbacks[event] ?: ArrayList()
        if (callback != null) {
            callbacks.remove(callback)
        } else {
            callbacks.clear()
        }
        this.callbacks[event] = callbacks
    }

    fun runCallbacks(event: String, args: Map<String, Any> = HashMap()) {
        val callbacks = this.callbacks[event] ?: ArrayList()
        callbacks.forEach { it.call(args) }
    }

    fun bind(event: String, callback: KFunction<Unit>) = addCallback(event, callback)
    fun unbind(event: String, callback: (KFunction<Unit>)?) = removeCallback(event, callback)
    fun trigger(event: String, args: Map<String, Any> = HashMap()) = runCallbacks(event, args)
}
