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
