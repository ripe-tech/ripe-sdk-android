package com.ripe.android.test

import com.ripe.android.base.Ripe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class APITest {
    @Suppress("experimental_api_usage")
    val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        @Suppress("experimental_api_usage")
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        @Suppress("experimental_api_usage")
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun testGetSizes() {
        runBlocking {
            val instance = Ripe(null, null)
            val deferred = instance.api.getSizesAsync()
            val result = deferred.await()!!

            @Suppress("unchecked_cast")
            val gendersFR = result["fr"] as List<String>
            @Suppress("unchecked_cast")
            val gendersUK = result["uk"] as List<String>
            assertEquals(gendersFR, listOf("female"))
            assertEquals(gendersUK, listOf("male", "female"))
        }
    }

    @Test
    fun testSizeToNative() {
        runBlocking {
            val instance = Ripe(null, null)
            val deferred = instance.api.sizeToNativeAsync("fr", 42.0, "female")
            val result = deferred.await()!!

            @Suppress("unchecked_cast")
            val scale = result["scale"] as String
            val value = result["value"] as Double
            val native = result["native"] as Double
            assertEquals(scale, "fr")
            assertEquals(value, 31.0, 0.0)
            assertEquals(native, 31.0, 0.0)
        }
    }

    @Test
    fun testSizeToNativeB() {
        runBlocking {
            val instance = Ripe(null, null)
            val deferred = instance.api.sizeToNativeBAsync(listOf("fr"), listOf(42.0), listOf("female"))
            val results = deferred.await()!!
            val result = results[0]
            @Suppress("unchecked_cast")
            val scale = result["scale"] as String
            val value = result["value"] as Double
            val native = result["native"] as Double
            assertEquals(scale, "fr")
            assertEquals(value, 31.0, 0.0)
            assertEquals(native, 31.0, 0.0)
        }
    }

    @Test
    fun testNativeToSize() {
        runBlocking {
            val instance = Ripe(null, null)
            val deferred = instance.api.nativeToSizeAsync("fr", 31, "female")
            val result = deferred.await()!!

            @Suppress("unchecked_cast")
            val value = result["value"] as Double
            assertEquals(value, 42.0, 0.0)
        }
    }

    @Test
    fun testNativeToSizeB() {
        runBlocking {
            val instance = Ripe(null, null)
            val deferred = instance.api.nativeToSizeBAsync(listOf("fr"), listOf(31), listOf("female"))
            val results = deferred.await()!!
            val result = results[0]
            @Suppress("unchecked_cast")
            val value = result["value"] as Double
            assertEquals(value, 42.0, 0.0)
        }
    }
}
