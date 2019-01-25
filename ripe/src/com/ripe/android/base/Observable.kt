package com.ripe.android.base

interface Observable {

    val callbacks: MutableMap<String, ArrayList<Function<Unit>>>

    fun addCallback(event: String, callback: Function<Unit>): Function<Unit>

    fun removeCallback(event: String, callback: Function<Unit>?)
}

class ObservableImpl : Observable {
    override val callbacks: MutableMap<String, ArrayList<Function<Unit>>> = HashMap()

    override fun addCallback(event: String, callback: Function<Unit>): Function<Unit> {
        val callbacks = this.callbacks[event] ?: ArrayList()
        callbacks.add(callback)
        this.callbacks[event] = callbacks
        return callback
    }

    override fun removeCallback(event: String, callback: Function<Unit>?) {
        val callbacks = this.callbacks[event] ?: ArrayList()
        if (callback != null) {
            callbacks.remove(callback)
        } else {
            callbacks.clear()
        }
        this.callbacks[event] = callbacks
    }
}