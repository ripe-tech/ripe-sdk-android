package com.ripe.android.base

open class Observable {
    val callbacks: MutableMap<String, ArrayList<(args: HashMap<String, Any>) -> Unit>> = HashMap()

    fun addCallback(event: String, callback: (args: HashMap<String, Any>) -> Unit): (args: HashMap<String, Any>) -> Unit {
        val callbacks = this.callbacks[event] ?: ArrayList()
        callbacks.add(callback)
        this.callbacks[event] = callbacks
        return callback
    }

    fun removeCallback(event: String, callback: ((args: HashMap<String, Any>) -> Unit)?) {
        val callbacks = this.callbacks[event] ?: ArrayList()
        if (callback != null) {
            callbacks.remove(callback)
        } else {
            callbacks.clear()
        }
        this.callbacks[event] = callbacks
    }

    fun runCallbacks(event: String, args: HashMap<String, Any> = HashMap()) {
        val callbacks = this.callbacks[event] ?: ArrayList()
        callbacks.forEach { it(args) }
    }

    fun bind(event: String, callback: (args: HashMap<String, Any>) -> Unit) = addCallback(event, callback)
    fun unbind(event: String, callback: ((args: HashMap<String, Any>) -> Unit)?) = removeCallback(event, callback)
    fun trigger(event: String, args: HashMap<String, Any> = HashMap()) = runCallbacks(event, args)
}
