package com.ripe.android.base

open class Observable {
    val callbacks: MutableMap<String, ArrayList<Function<Unit>>> = HashMap()

    fun addCallback(event: String, callback: Function<Unit>): Function<Unit> {
        val callbacks = this.callbacks[event] ?: ArrayList()
        callbacks.add(callback)
        this.callbacks[event] = callbacks
        return callback
    }

    fun removeCallback(event: String, callback: Function<Unit>?) {
        val callbacks = this.callbacks[event] ?: ArrayList()
        if (callback != null) {
            callbacks.remove(callback)
        } else {
            callbacks.clear()
        }
        this.callbacks[event] = callbacks
    }
}
