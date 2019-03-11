package com.ripe.android.api

import kotlinx.coroutines.Deferred

interface SizeAPI : BaseAPI {
    fun getSizesAsync(options: Map<String, Any> = HashMap()): Deferred<Map<String, Any>?> {
        var url = this.getUrl() + "sizes"
        var sizeOptions: MutableMap<String, Any> = mutableMapOf(
                "url" to url,
                "method" to "GET"
        )
        sizeOptions.putAll(options)
        sizeOptions = this.build(sizeOptions).toMutableMap()
        url = sizeOptions["url"] as String
        return this.cacheURLAsync(url, options)
    }

    fun sizeToNativeAsync(scale: String, value: Double, gender: String, options: Map<String, Any> = HashMap()): Deferred<Map<String, Any>?> {
        var url = this.getUrl() + "sizes/size_to_native"
        var sizeOptions = mutableMapOf(
                "url" to url,
                "method" to "GET",
                "params" to mapOf(
                        "scale" to scale,
                        "value" to value,
                        "gender" to gender
                )
        )
        sizeOptions.putAll(options)
        sizeOptions = this.build(sizeOptions).toMutableMap()
        url = sizeOptions["url"] as String
        return this.cacheURLAsync(url, sizeOptions)
    }

    fun sizeToNativeBAsync(scales: List<String>, values: List<Double>, genders: List<String>, options: Map<String, Any> = HashMap()): Deferred<List<Map<String, Any>>?> {
        var url = this.getUrl() + "sizes/size_to_native_b"

        val scalesP = ArrayList<String>()
        val valuesP = ArrayList<Double>()
        val gendersP = ArrayList<String>()

        for (index in 0 until scales.size) {
            scalesP.add(scales[index])
            valuesP.add(values[index])
            gendersP.add(genders[index])
        }

        var sizeOptions = mutableMapOf(
                "url" to url,
                "method" to "GET",
                "params" to mapOf(
                        "scales" to scales,
                        "values" to values,
                        "genders" to genders
                )
        )
        sizeOptions.putAll(options)
        sizeOptions = this.build(sizeOptions).toMutableMap()
        url = sizeOptions["url"] as String
        return this.cacheURLAsync(url, sizeOptions)
    }

    fun nativeToSizeAsync(scale: String, value: Int, gender: String, options: Map<String, Any> = HashMap()): Deferred<Map<String, Any>?> {
        var url = this.getUrl() + "sizes/native_to_size"
        var sizeOptions = mutableMapOf(
                "url" to url,
                "method" to "GET",
                "params" to mapOf(
                        "scale" to scale,
                        "value" to value,
                        "gender" to gender
                )
        )
        sizeOptions.putAll(options)
        sizeOptions = this.build(sizeOptions).toMutableMap()
        url = sizeOptions["url"] as String
        return this.cacheURLAsync(url, sizeOptions)
    }

    fun nativeToSizeBAsync(scales: List<String>, values: List<Int>, genders: List<String>, options: Map<String, Any> = HashMap()): Deferred<List<Map<String, Any>>?> {
        var url = this.getUrl() + "sizes/native_to_size_b"

        val scalesP = ArrayList<String>()
        val valuesP = ArrayList<Int>()
        val gendersP = ArrayList<String>()


        for (index in 0 until scales.size) {
            scalesP.add(scales[index])
            valuesP.add(values[index])
            gendersP.add(genders[index])
        }

        var sizeOptions = mutableMapOf(
                "url" to url,
                "method" to "GET",
                "params" to mapOf(
                        "scales" to scales,
                        "values" to values,
                        "genders" to genders
                )
        )
        sizeOptions.putAll(options)
        sizeOptions = this.build(sizeOptions).toMutableMap()
        url = sizeOptions["url"] as String
        return this.cacheURLAsync(url, sizeOptions)
    }
}
