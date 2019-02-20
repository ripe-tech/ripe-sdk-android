package com.ripe.android.test

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import com.ripe.android.base.Observable


class ObservableTest {

    @Test
    fun testAddRemove() {
        val observable = Observable()
        val callback = observable.bind("test") {}

        assertNotNull(observable.callbacks["test"])
        assertEquals(observable.callbacks["test"]!!.size, 1)

        observable.trigger("test", hashMapOf("a" to "a", "b" to "b"))

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
                return@async "delay"}
        }

        assertNotNull(observable.callbacks["test"])
        assertEquals(observable.callbacks["test"]!!.size, 2)

        val deferreds = observable.trigger("test")

        runBlocking {
            val results = ArrayList<String>()
            deferreds.forEach {
                val result = it.await() as String
                results.add(result)
            }
            assertEquals(results[0], "result")
            assertEquals(results[1], "delay")
        }
    }
}
