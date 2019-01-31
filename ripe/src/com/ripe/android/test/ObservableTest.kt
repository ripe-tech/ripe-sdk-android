package com.ripe.android.test

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

import com.ripe.android.base.Observable

class ObservableTest {

    @Test
    fun testAddRemove() {
        val observable = Observable()
        val callback = observable.addCallback("test") { a: String, b: String -> print(a + b) }
        assertNotNull(observable.callbacks["test"])
        assertEquals(observable.callbacks["test"]!!.size, 1)
        observable.removeCallback("test", callback)
        assertEquals(observable.callbacks["test"]!!.size, 0)
    }
}
