package com.ripe.android.test

import com.ripe.android.base.Ripe
import com.ripe.android.plugins.SyncPlugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SyncTest : BaseTest() {
    @Test
    fun testStringSync() {
        val syncPlugin = SyncPlugin(mapOf("full" to listOf("upper", "bottom")))
        val instance = Ripe(null, null, mapOf("plugins" to listOf(syncPlugin)))
        val initialParts = mapOf(
            "upper" to mapOf("material" to "nappa", "color" to "black"),
            "bottom" to mapOf("material" to "nappa", "color" to "black")
        )
        instance.loadedConfig = mapOf("defaults" to initialParts)
        instance.parts = initialParts.toMutableMap()

        instance.trigger("part")
        @Suppress("unchecked_cast")
        var upper = instance.parts["upper"] as Map<String, String>
        @Suppress("unchecked_cast")
        var bottom = instance.parts["bottom"] as Map<String, String>
        assertEquals(upper["color"], "black")
        assertEquals(bottom["color"], "black")

        instance.setPart("bottom", "nappa", "white")
        @Suppress("unchecked_cast")
        upper = instance.parts["upper"] as Map<String, String>
        @Suppress("unchecked_cast")
        bottom = instance.parts["bottom"] as Map<String, String>
        assertEquals(upper["color"], "white")
        assertEquals(bottom["color"], "white")
    }

    @Test
    fun testConfigSync() {
        runBlocking {
            launch(Dispatchers.Main) {
                val syncPlugin = SyncPlugin()
                val instance = Ripe("swear", "vyner", mapOf("plugins" to listOf(syncPlugin)))
                waitForEvent(instance,"post_config")

                @Suppress("unchecked_cast")
                var hardware = instance.parts["hardware"] as Map<String, String>
                @Suppress("unchecked_cast")
                var logo = instance.parts["logo"] as Map<String, String>
                assertEquals(hardware["color"], "silver")
                assertEquals(logo["color"], "silver")

                instance.setPart("hardware", "metal", "bronze")

                @Suppress("unchecked_cast")
                hardware = instance.parts["hardware"] as Map<String, String>
                @Suppress("unchecked_cast")
                logo = instance.parts["logo"] as Map<String, String>
                assertEquals(hardware["color"], "bronze")
                assertEquals(logo["color"], "bronze")

                instance.config("swear", "maltby")

                @Suppress("unchecked_cast")
                var fringeHardware = instance.parts["fringe_hardware"] as Map<String, String>
                @Suppress("unchecked_cast")
                logo = instance.parts["logo"] as Map<String, String>
                assertEquals(fringeHardware["color"], "silver")
                assertEquals(logo["color"], "silver")

                instance.setPart("fringe_hardware", "metal", "bronze")

                @Suppress("unchecked_cast")
                fringeHardware = instance.parts["fringe_hardware"] as Map<String, String>
                @Suppress("unchecked_cast")
                logo = instance.parts["logo"] as Map<String, String>
                assertEquals(fringeHardware["color"], "bronze")
                assertEquals(logo["color"], "bronze")
            }
        }
    }
}
