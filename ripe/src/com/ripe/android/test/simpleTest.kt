package com.ripe.android.test

import org.junit.Test
import org.junit.Assert.assertEquals
import com.ripe.android.base.Ripe


class SimpleTest {

    @Test
    fun testInstance() {
        val instance = Ripe(null, null)
        assertEquals(instance.initials, "")
        assertEquals(instance.engraving, "")
        assertEquals(instance.children.size, 0)
        assertEquals(instance.ready, true)
    }
}
