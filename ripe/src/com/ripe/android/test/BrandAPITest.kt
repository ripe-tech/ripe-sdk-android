package com.ripe.android.test

import com.ripe.android.api.BrandAPI
import kotlinx.coroutines.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BrandAPITest : BaseTest(){

    @Test
    fun testGetLogoOptions(){
        val brandApi = BrandAPI()
        val options = brandApi.getLogoOptions()
        assertEquals(options,
        mapOf("url" to url,
        "method" to "GET",
        "params" to params))
    }

    @Test
    fun testGetMeshOptions(){
        val brandApi = BrandAPI()
        val options = brandApi.getMeshOptions()
        assertEquals(options,
        mapOf("url" to url,
        "method" to "GET",
        "params" to params))
    }
}