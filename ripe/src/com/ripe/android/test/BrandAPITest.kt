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
    fun testGetLogoOptionsDefaults(){
        val brandApi = MockBrandAPI()
        val result = brandApi.getLogoOptions()
        assertEquals(result,
            mapOf(
                "url" to "https://sandbox.platforme.com/api/brands/dummy/logo.png",
                "method" to "GET",
                "params" to HashMap<String, Any>()
            )
        )
    }

    @Test
    fun testGetLogoOptions(){
        val brandApi = MockBrandAPI()
        val result = brandApi.getLogoOptions(mapOf(
            "brand" to "dummy",
            "version" to "version",
            "variant" to "variant",
            "format" to "svg"
        ))
        assertEquals(result,
            mapOf(
                "url" to "https://sandbox.platforme.com/api/brands/dummy/logo.svg",
                "method" to "GET",
                "params" to mapOf(
                    "version" to "version",
                    "variant" to "variant"
                ),
                "brand" to "dummy",
                "version" to "version",
                "variant" to "variant",
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