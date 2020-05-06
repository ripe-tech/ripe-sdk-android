package com.ripe.android.test

import com.ripe.android.base.Observable
import kotlinx.coroutines.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test


class ObservableTest : BaseTest() {

    @Test
    fun testAddRemove() {
        val observable = Observable()
        val callback = observable.bind("test") {}

        assertNotNull(observable.callbacks["test"])
        assertEquals(observable.callbacks["test"]!!.size, 1)

        observable.unbind("test", callback)
        assertEquals(observable.callbacks["test"]!!.size, 0)
    }

    @Test
    fun testAsyncEvents() {
        val observable = Observable()
        observable.bindAsync("test") {
            GlobalScope.async { "result" }
        }

        observable.bindAsync("test") {
            GlobalScope.async {
                delay(100)
                return@async "delay"
            }
        }

        assertNotNull(observable.callbacks["test"])
        assertEquals(observable.callbacks["test"]!!.size, 2)

        val deferreds = observable.trigger("test")

        runBlocking {
            launch(Dispatchers.Main) {
                val results = deferreds.await()
                assertEquals(results[0], "result")
                assertEquals(results[1], "delay")
            }
        }
    }
}
