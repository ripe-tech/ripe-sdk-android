package com.ripe.android.test

import com.ripe.android.base.Ripe
import com.ripe.android.api.BrandAPI
import kotlinx.coroutines.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MockBrandAPI : BrandAPI {
    override val owner = Ripe("dummy", "dummy")
}

class BrandAPITest : BaseTest(){

    @Test
    fun testGetLogoOptions(){
        val brandApi = MockBrandAPI()

        val options = brandApi.getLogoOptions()
        assertEquals(options,
            mapOf(
                "url" to "https://sandbox.platforme.com/api/brands/dummy/logo.png",
                "method" to "GET",
                "params" to HashMap<String, Any>()
            )
        )

        val fields: Map<String, Any> = mapOf(
            "brand" to "dummy",
            "version" to "dummy",
            "variant" to "dummy",
            "format" to "svg"
        )
        val options2 = brandApi.getLogoOptions(fields)
        assertEquals(options2,
            mapOf(
                "url" to "https://sandbox.platforme.com/api/brands/dummy/logo.svg",
                "method" to "GET",
                "params" to mapOf(
                    "version" to "dummy",
                    "variant" to "dummy"
                ),
                "brand" to "dummy",
                "version" to "dummy",
                "variant" to "dummy",
                "format" to "svg"
            )
        )
    }

    @Test
    fun testGetMeshOptions(){
        val brandApi = MockBrandAPI()
        val options = brandApi.getMeshOptions()

        assertEquals(options,
            mapOf(
                "url" to "https://sandbox.platforme.com/api/brands/dummy/models/dummy/mesh",
                "method" to "GET",
                "params" to HashMap<String, Any>()
            )
        )
    }
}