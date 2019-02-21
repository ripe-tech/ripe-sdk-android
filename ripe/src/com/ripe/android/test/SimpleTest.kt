package com.ripe.android.test

import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.isActive
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import org.junit.Test
import org.junit.After
import org.junit.Before
import org.junit.Assert.assertEquals
import com.ripe.android.base.Ripe
import com.ripe.android.base.Observable


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

    suspend fun waitForEvent(instance: Observable, event: String) = suspendCoroutine<Any> {
        continuation -> instance.bind(event) {
            result -> if(continuation.context.isActive) continuation.resume(result)
        }
    }

}
