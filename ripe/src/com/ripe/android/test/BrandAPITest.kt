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

        var options = brandApi.getLogoOptions()
        assertEquals(options,
            mapOf(
                "url" to "https://sandbox.platforme.com/api/brands/dummy/models/dummy/logo",
                "method" to "GET",
                "params" to HashMap<String, Any>()
            )
        )

        options = brandApi.getLogoOptions(mapOf(
            "variant" to "variant"
        ))
        assertEquals(options,
            mapOf(
                "url" to "https://sandbox.platforme.com/api/brands/dummy/models/dummy/logo",
                "method" to "GET",
                "params" to mapOf(
                    "variant" to "variant"
                )
            )
        )
    }

    @Test
    fun testGetMeshOptions(){
        // val brandApi = BrandAPI()
        // val options = brandApi.getMeshOptions()
        // assertEquals(options,
        // mapOf("url" to url,
        // "method" to "GET",
        // "params" to params))
    }
}