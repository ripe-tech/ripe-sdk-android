package com.ripe.android.test

import com.ripe.android.base.Observable
import com.ripe.android.base.Ripe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.resume


class SimpleTest {
    @Suppress("experimental_api_usage")
    val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun testInstance() {
        runBlocking {
            val instance = Ripe(null, null)
            assertEquals(instance.initials, "")
            assertEquals(instance.engraving, "")
            assertEquals(instance.children.size, 0)
            assertEquals(instance.ready, false)

            instance.config("dummy", "dummy")
            assertEquals(instance.ready, true)
        }
    }

    @Test
    fun testInstanceValues() {
        runBlocking {
            val instance = Ripe("dummy", "dummy")
            waitForEvent(instance, "config")

            @Suppress("unchecked_cast")
            val result = waitForEvent(instance, "price") as Map<String, Any>
            @Suppress("unchecked_cast")
            var total = result["total"] as Map<String, Any>
            var priceFinal = total["price_final"] as Double
            var country = total["country"] as String
            var currency = total["currency"] as String
            assertEquals(priceFinal > 0, true)
            assertEquals(country, "US")
            assertEquals(currency, "EUR")

            val price = instance.api.getPriceAsync().await()
            @Suppress("unchecked_cast")
            total = price!!["total"] as Map<String, Any>
            priceFinal = total["price_final"] as Double
            country = total["country"] as String
            currency = total["currency"] as String
            assertEquals(priceFinal > 0, true)
            assertEquals(country, "US")
            assertEquals(currency, "EUR")
        }
    }

    @Test
    fun testUndoSetParts() {
        runBlocking {
            val instance = Ripe("swear", "vyner")
            @Suppress("unchecked_cast")
            val result = waitForEvent(instance, "post_parts") as Map<String, Any>
            @Suppress("unchecked_cast")
            val initialParts = result["parts"] as Map<String, Any>

            assertEquals(instance.getParts(), initialParts)
            assertEquals(instance.canUndo(), false)
            assertEquals(instance.canRedo(), false)

            instance.undo()
            assertEquals(instance.canUndo(), false)
            assertEquals(instance.canRedo(), false)

            var parts = instance.getParts()
            assertEquals(parts, initialParts)

            @Suppress("unchecked_cast")
            var front = parts["front"] as Map<String, String>
            assertEquals(front["material"], "nappa")
            assertEquals(front["color"], "white")

            instance.setPart("front", "suede", "black")
            parts = instance.getParts()
            front = parts["front"] as Map<String, String>
            assertEquals(front["material"], "suede")
            assertEquals(front["color"], "black")
            assertEquals(instance.canUndo(), true)
            assertEquals(instance.canRedo(), false)

            instance.undo()

            parts = instance.getParts()
            @Suppress("unchecked_cast")
            front = parts["front"] as Map<String, String>
            assertEquals(parts, initialParts)
            assertEquals(front["material"], "nappa")
            assertEquals(front["color"], "white")
            assertEquals(instance.canUndo(), false)
            assertEquals(instance.canRedo(), true)

            instance.redo()

            parts = instance.getParts()
            @Suppress("unchecked_cast")
            front = parts["front"] as Map<String, String>
            assertEquals(front["material"], "suede")
            assertEquals(front["color"], "black")
            assertEquals(instance.canUndo(), true)
            assertEquals(instance.canRedo(), false)
        }
    }

    suspend fun waitForEvent(instance: Observable, event: String) = suspendCancellableCoroutine<Any> { continuation ->
        instance.bind(event) { result ->
            if (continuation.isActive) {
                continuation.resume(result)
                continuation.cancel()
            }
        }
    }

}
