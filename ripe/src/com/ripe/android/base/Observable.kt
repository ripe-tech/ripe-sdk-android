package com.ripe.android.base

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async

/**
 * A function to be executed when an event. It receives a dictionary with the response as parameter and returns a deferred result which is completed asynchronously.
 *
 * @param response A dictionary with the payload of the event
 * @return A Deferred that may be completed asynchronously
 */
typealias ObservableCallback = (args: Map<String, Any>) -> Deferred<Any?>?

/**
 * An object that emits events. Listeners can bind to specific events and be notified when the event is triggered.
 */
open class Observable {

    /**
     * A dictionary that contains all methods to be executed when an event is triggered
     */
    val callbacks: MutableMap<String, MutableList<ObservableCallback>> = HashMap()

    /**
     * Binds to an event by providing a block that will receive the event payload as a
     * parameter and return a Deferred that will be completed asynchronously.
     *
     * @param event Name of the event to bind to.
     * @param callback Block to be executed when the event is triggered.
     * @return Returns the provided callback, to be used when unbinding from the event.
     */
    fun addCallback(event: String, callback: ObservableCallback): ObservableCallback {
        val callbacks = this.callbacks[event] ?: ArrayList()
        callbacks.add(callback)
        this.callbacks[event] = callbacks
        return callback
    }

    /**
     * Unbinds the provided callback from an event.
     *
     * @param event The name of the event.
     * @param callback The callback that was returned when the bind method was called.
     */
    fun removeCallback(event: String, callback: ObservableCallback?) {
        val callbacks = this.callbacks[event] ?: ArrayList()
        if (callback != null) {
            callbacks.remove(callback)
        } else {
            callbacks.clear()
        }
        this.callbacks[event] = callbacks
    }

    /**
     * Triggers the event by calling all its bound callbacks with args as parameters.
     *
     * @param event The name of the event to be triggered.
     * @param args The payload of the event, to be passed to the callbacks.
     * @return Returns a deferred result that will be completed when all of the callbacks
     * have finished processing the triggered event.
     */
    @JvmOverloads
    fun runCallbacks(event: String, args: Map<String, Any> = HashMap()): Deferred<List<Any?>> {
        val callbacks = this.callbacks[event] ?: ArrayList()
        val deferreds = callbacks.map { it.invoke(args) }.filter { it != null }
        @Suppress("experimental_api_usage")
        return MainScope().async { deferreds.map { it!!.await() } }
    }

    /**
     * Binds to an event by providing a block that will receive the event payload as a
     * parameter. The block will be executed synchronously, for costly operations prefer
     * the use of `bindAsync`.
     *
     * @param event Name of the event to bind to.
     * @param callback Block to be executed synchronously when the event is triggered.
     * @return Returns the callback instance created, to be used when unbinding from the event.
     */
    fun bind(event: String, callback: (args: Map<String, Any>) -> Unit) = bindSync(event, callback)

    /**
     * Binds to an event by providing a block that will receive the event payload as a
     * parameter. The block will be executed synchronously, for costly operations prefer
     * the use of `bindAsync`.
     *
     * @param event Name of the event to bind to.
     * @param callback Block to be executed synchronously when the event is triggered.
     * @return Returns the callback instance created, to be used when unbinding from the event.
     */
    fun bindSync(event: String, callback: (args: Map<String, Any>) -> Unit) = addCallback(event) { args ->
        callback(args)
        return@addCallback null
    }

    /**
     * Binds to an event by providing a block that will receive the event payload as a
     * parameter and return a Deferred that will be completed asynchronously.
     *
     * @param event Name of the event to bind to.
     * @param callback Block to be executed when the event is triggered.
     * @return Returns the provided callback, to be used when unbinding from the event.
     */
    fun bindAsync(event: String, callback: ObservableCallback) = addCallback(event, callback)

    /**
     * Unbinds the provided callback from an event.
     *
     * @param event The name of the event.
     * @param callback The callback that was returned when the bind method was called.
     */
    fun unbind(event: String, callback: ObservableCallback?) = removeCallback(event, callback)

    /**
     * Triggers the event by calling all its bound callbacks with args as parameters.
     *
     * @param event The name of the event to be triggered.
     * @param args The payload of the event, to be passed to the callbacks.
     * @return Returns a deferred result that will be completed when all of the callbacks
     * have finished processing the triggered event.
     */
    @JvmOverloads
    fun trigger(event: String, args: Map<String, Any> = HashMap()) = runCallbacks(event, args)
}
