package com.ripe.android.test

import com.ripe.android.base.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.AfterClass
import org.junit.BeforeClass
import kotlin.coroutines.resume

open class BaseTest {

    class MockRipe: Observable() {
        var parts: MutableMap<String, Any> = HashMap()

        fun setPart(part: String, material: String, color: String) {
            val partValue = mapOf("material" to material, "color" to color)
            this.parts[part] = partValue
            this.trigger("part", mapOf())
        }

    }

    companion object {
        @Suppress("experimental_api_usage")
        val mainThreadSurrogate = newSingleThreadContext("UI thread")

        @BeforeClass
        @JvmStatic
        fun setUp() {
            @Suppress("experimental_api_usage")
            Dispatchers.setMain(mainThreadSurrogate)
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            @Suppress("experimental_api_usage")
            Dispatchers.resetMain()
            mainThreadSurrogate.close()
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
