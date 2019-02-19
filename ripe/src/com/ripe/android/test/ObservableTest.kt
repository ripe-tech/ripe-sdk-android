package com.ripe.android.test

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

import com.ripe.android.base.Observable
import kotlin.reflect.KFunction

class ObservableTest {

    @Test
    fun testAddRemove() {
        val observable = Observable()
        observable.addCallback("test", callback = KFunction)
        val callback = observable.addCallback("test") {}
        assertNotNull(observable.callbacks["test"])
        assertEquals(observable.callbacks["test"]!!.size, 1)

        observable.trigger("test", hashMapOf("a" to "a", "b" to "b"))

        observable.removeCallback("test", callback)
        assertEquals(observable.callbacks["test"]!!.size, 0)
    }
}
