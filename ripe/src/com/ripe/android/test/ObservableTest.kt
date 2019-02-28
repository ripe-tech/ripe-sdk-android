package com.ripe.android.test

import org.junit.Test
import org.junit.Before
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import com.ripe.android.base.Observable


class ObservableTest {

    @Before
    fun setUp() {
        @Suppress("experimental_api_usage")
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

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
                return@async "delay"}
        }

        assertNotNull(observable.callbacks["test"])
        assertEquals(observable.callbacks["test"]!!.size, 2)

        val deferreds = observable.trigger("test")

        runBlocking {
            val results = deferreds.await()
            assertEquals(results[0], "result")
            assertEquals(results[1], "delay")
        }
    }
}
