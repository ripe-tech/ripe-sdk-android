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
    fun testGetLogoUrl(){
        val brandApi = MockBrandAPI()
        val result = brandApi.getLogoUrl(
            mapOf(
                    "brand" to "dummy",
                    "version" to "version",
                    "variant" to "variant",
                    "format" to "svg"
                )
        )
        assertEquals(result,
        "https://sandbox.platforme.com/api/brands/dummy/logo.svg?variant=variant&version=version")
    }

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
        val result = brandApi.getLogoOptions(
            mapOf(
            "brand" to "dummy",
            "version" to "version",
            "variant" to "variant",
            "format" to "svg"
            )
        )
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
    fun testGetMeshOptionsDefaults(){
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

    @Test
    fun testGetMeshOptions(){
        val brandApi = MockBrandAPI()
        val options = brandApi.getMeshOptions(
            mapOf(
            "brand" to "dummy",
            "version" to "version",
            "variant" to "variant"
            )
        )

        assertEquals(options,
            mapOf(
                "url" to "https://sandbox.platforme.com/api/brands/dummy/models/dummy/mesh",
                "method" to "GET",
                "params" to mapOf(
                    "version" to "version",
                    "variant" to "variant"
                ),
                "brand" to "dummy",
                "version" to "version",
                "variant" to "variant"
            )
        )
    }

    @Test
    fun testGetConfigOptionsDefaults(){
        val brandApi = MockBrandAPI()
        val options = brandApi.getConfigOptions()

        assertEquals(options,
            mapOf(
                "url" to "https://sandbox.platforme.com/api/brands/dummy/models/dummy/config",
                "method" to "GET",
                "params" to HashMap<String, Any>()
            )
        )
    }

    @Test
    fun testGetConfigOptions(){
        val brandApi = MockBrandAPI()
        val options = brandApi.getConfigOptions(
            mapOf(
            "brand" to "dummy",
            "model" to "model",
            "country" to "country",
            "country" to "country",
            "flag" to "flag",
            "filter" to "0"
            )
        )
        assertEquals(options,
            mapOf(
                "url" to "https://sandbox.platforme.com/api/brands/dummy/models/model/config",
                "method" to "GET",
                "params" to mapOf(
                    "country" to "country",
                    "flag" to "flag"
                ),
                "brand" to "dummy",
                "model" to "model",
                "country" to "country",
                "flag" to "flag",
                "filter" to "0"
            )
        )
    }
}