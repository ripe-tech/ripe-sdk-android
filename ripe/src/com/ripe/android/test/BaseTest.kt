package com.ripe.android.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.AfterClass
import org.junit.BeforeClass

open class BaseTest {
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