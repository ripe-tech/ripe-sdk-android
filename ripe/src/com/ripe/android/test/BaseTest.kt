package com.ripe.android.test

import com.ripe.android.base.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.AfterClass
import org.junit.BeforeClass

open class BaseTest {

    class MockRipe : Observable() {
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
}
