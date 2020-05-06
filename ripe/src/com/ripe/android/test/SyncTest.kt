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
    fun testStringRuleSync() {
        runBlocking {
            launch(Dispatchers.Main) {
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
        }
    }

    @Test
    fun testObjectRuleSync() {
        runBlocking {
            launch(Dispatchers.Main) {
                val syncPlugin = SyncPlugin(mapOf("full" to listOf(
                        mapOf("part" to "upper"),
                        mapOf("part" to "bottom")
                )))
                val instance = Ripe(null, null, mapOf("plugins" to listOf(syncPlugin)))
                val initialParts = mapOf(
                        "upper" to mapOf("material" to "nappa", "color" to "black"),
                        "bottom" to mapOf("material" to "suede", "color" to "white")
                )
                instance.loadedConfig = mapOf("defaults" to initialParts)
                instance.parts = initialParts.toMutableMap()

                instance.trigger("part")
                @Suppress("unchecked_cast")
                var bottom = instance.parts["bottom"] as Map<String, String>
                assertEquals(bottom["material"], "nappa")
                assertEquals(bottom["color"], "black")

                instance.setPart("bottom", "suede", "white")

                @Suppress("unchecked_cast")
                val upper = instance.parts["upper"] as Map<String, String>
                @Suppress("unchecked_cast")
                bottom = instance.parts["bottom"] as Map<String, String>
                assertEquals(bottom["material"], "suede")
                assertEquals(bottom["color"], "white")
                assertEquals(upper["material"], "suede")
                assertEquals(upper["color"], "white")
            }
        }
    }

    @Test
    fun testPartMaterialSync() {
        runBlocking {
            launch(Dispatchers.Main) {
                val syncPlugin = SyncPlugin(mapOf("full" to listOf(
                        mapOf("part" to "upper", "material" to "nappa"),
                        mapOf("part" to "bottom", "material" to "suede")
                )))
                val instance = Ripe(null, null, mapOf("plugins" to listOf(syncPlugin)))
                val initialParts = mapOf(
                        "upper" to mapOf("material" to "nappa", "color" to "black"),
                        "bottom" to mapOf("material" to "leather", "color" to "black")
                )
                instance.loadedConfig = mapOf("defaults" to initialParts)
                instance.parts = initialParts.toMutableMap()

                instance.trigger("part")
                @Suppress("unchecked_cast")
                var bottom = instance.parts["bottom"] as Map<String, String>
                assertEquals(bottom["material"], "suede")

                instance.setPart("bottom", "suede", "white")

                @Suppress("unchecked_cast")
                val upper = instance.parts["upper"] as Map<String, String>
                @Suppress("unchecked_cast")
                bottom = instance.parts["bottom"] as Map<String, String>
                assertEquals(bottom["material"], "suede")
                assertEquals(bottom["color"], "white")
                assertEquals(upper["material"], "nappa")
                assertEquals(upper["color"], "white")
            }
        }
    }

    @Test
    fun testPartColorSync() {
        runBlocking {
            launch(Dispatchers.Main) {
                val syncPlugin = SyncPlugin(mapOf("full" to listOf(
                        mapOf("part" to "upper", "color" to "white"),
                        mapOf("part" to "bottom", "color" to "white")
                )))
                val instance = Ripe(null, null, mapOf("plugins" to listOf(syncPlugin)))
                val initialParts = mapOf(
                        "upper" to mapOf("material" to "nappa", "color" to "black"),
                        "bottom" to mapOf("material" to "suede", "color" to "black")
                )
                instance.loadedConfig = mapOf("defaults" to initialParts)
                instance.parts = initialParts.toMutableMap()

                instance.trigger("part")
                assertEquals(instance.getPartsCopy(), initialParts)

                instance.setPart("bottom", "suede", "red")

                @Suppress("unchecked_cast")
                var bottom = instance.parts["bottom"] as Map<String, String>

                @Suppress("unchecked_cast")
                var upper = instance.parts["upper"] as Map<String, String>
                assertEquals(bottom["material"], "suede")
                assertEquals(bottom["color"], "red")
                assertEquals(upper["material"], "nappa")
                assertEquals(upper["color"], "black")

                instance.setPart("bottom", "suede", "white")

                @Suppress("unchecked_cast")
                bottom = instance.parts["bottom"] as Map<String, String>
                @Suppress("unchecked_cast")
                upper = instance.parts["upper"] as Map<String, String>
                assertEquals(bottom["material"], "suede")
                assertEquals(bottom["color"], "white")
                assertEquals(upper["material"], "nappa")
                assertEquals(upper["color"], "white")
            }
        }
    }


    @Test
    fun testPartMaterialColorSync() {
        runBlocking {
            launch(Dispatchers.Main) {
                val syncPlugin = SyncPlugin(mapOf("full" to listOf(
                        mapOf("part" to "upper", "material" to "nappa", "color" to "green"),
                        mapOf("part" to "bottom", "material" to "suede", "color" to "red")
                )))
                val instance = Ripe(null, null, mapOf("plugins" to listOf(syncPlugin)))
                val initialParts = mapOf(
                        "upper" to mapOf("material" to "nappa", "color" to "black"),
                        "bottom" to mapOf("material" to "suede", "color" to "black")
                )
                instance.loadedConfig = mapOf("defaults" to initialParts)
                instance.parts = initialParts.toMutableMap()

                instance.trigger("part")
                assertEquals(instance.getPartsCopy(), initialParts)

                instance.setPart("bottom", "suede", "red")

                @Suppress("unchecked_cast")
                val bottom = instance.parts["bottom"] as Map<String, String>

                @Suppress("unchecked_cast")
                val upper = instance.parts["upper"] as Map<String, String>
                assertEquals(bottom["material"], "suede")
                assertEquals(bottom["color"], "red")
                assertEquals(upper["material"], "nappa")
                assertEquals(upper["color"], "green")
            }
        }
    }

    @Test
    fun testConfigSync() {
        runBlocking {
            launch(Dispatchers.Main) {
                val syncPlugin = SyncPlugin()
                val instance = Ripe(null, null, mapOf("plugins" to listOf(syncPlugin)))
                val initialParts = mapOf(
                        "upper" to mapOf("material" to "nappa", "color" to "black"),
                        "bottom" to mapOf("material" to "nappa", "color" to "black")
                )
                val config = mapOf(
                        "defaults" to initialParts,
                        "sync" to mapOf("full" to listOf("upper", "bottom"))
                )
                instance.loadedConfig = config
                instance.parts = initialParts.toMutableMap()
                instance.addPlugin(syncPlugin)

                instance.trigger("post_config", config)
                instance.trigger("part")
                assertEquals(instance.getPartsCopy(), initialParts)

                instance.setPart("bottom", "nappa", "white")

                @Suppress("unchecked_cast")
                val bottom = instance.parts["bottom"] as Map<String, String>

                @Suppress("unchecked_cast")
                val upper = instance.parts["upper"] as Map<String, String>
                assertEquals(bottom["color"], "white")
                assertEquals(upper["color"], "white")
            }
        }
    }

    @Test
    fun testConfigChangeSync() {
        runBlocking {
            launch(Dispatchers.Main) {
                val syncPlugin = SyncPlugin()
                val instance = Ripe("swear", "vyner", mapOf("plugins" to listOf(syncPlugin)))
                waitForEvent(instance, "post_config")

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
