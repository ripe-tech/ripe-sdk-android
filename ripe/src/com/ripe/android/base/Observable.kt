package com.ripe.android.base

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async

typealias ObservableCallback = (args: Map<String, Any>) -> Deferred<Any?>?

open class Observable {

    val callbacks: MutableMap<String, MutableList<ObservableCallback>> = HashMap()

    fun addCallback(event: String, callback: ObservableCallback): ObservableCallback {
        val callbacks = this.callbacks[event] ?: ArrayList()
        callbacks.add(callback)
        this.callbacks[event] = callbacks
        return callback
    }

    fun removeCallback(event: String, callback: ObservableCallback?) {
        val callbacks = this.callbacks[event] ?: ArrayList()
        if (callback != null) {
            callbacks.remove(callback)
        } else {
            callbacks.clear()
        }
        this.callbacks[event] = callbacks
    }

    fun runCallbacks(event: String, args: Map<String, Any> = HashMap()): Deferred<List<Any?>> {
        val callbacks = this.callbacks[event] ?: ArrayList()
        val deferreds = callbacks.map { it.invoke(args) }.filter { it != null }
        @Suppress("experimental_api_usage")
        return MainScope().async { deferreds.map {  it!!.await() } }
    }

    fun bind(event: String, callback: (args: Map<String, Any>) -> Unit) = bindSync(event, callback)

    fun bindSync(event: String, callback: (args: Map<String, Any>) -> Unit) = addCallback(event) { args ->
        callback(args)
        return@addCallback null
    }

    fun bindAsync(event: String, callback: ObservableCallback) = addCallback(event, callback)

    fun unbind(event: String, callback: ObservableCallback?) = removeCallback(event, callback)

    fun trigger(event: String, args: Map<String, Any> = HashMap()) = runCallbacks(event, args)
}
