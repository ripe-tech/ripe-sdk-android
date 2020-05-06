package com.ripe.android.api

import kotlinx.coroutines.Deferred

/**
 * The interface for the Size API.
 */
interface SizeAPI : BaseAPI {
    /**
     * Provides a list of all the available size scales.
     * This can be used to know what scales are available for size conversions.
     * @param options A map with options to configure the request.
     * @return A Deferred that will be completed with the sizes map.
     */
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

    /**
     * Converts a size value from the native scale to the corresponding value in the specified scale.
     * The available scales, genders and sizes can be obtained with the method [getSizesAsync].
     *
     * @param scale The scale which one wants to convert to.
     * @param value The value which one wants to convert.
     * @param gender The gender of the scale and value to be converted.
     * @return A Deferred that will be completed with the converted value.
     */
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

    /**
     * Converts multiple size values from the native scale to the corresponding values in the specified scales.
     * The available scales, genders and sizes can be obtained with the method [getSizesAsync].
     *
     * @param scales A list of scales to convert to.
     * @param values A list of values to convert.
     * @param genders A list of genders corresponding to the values.
     * @param options A map with options to configure the request.
     * @return A Deferred that will be completed with a list with the converted values.
     */
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

    /**
     * Converts a size value in the specified scale to the corresponding native size.
     * The available scales, genders and sizes can be obtained with the method [getPriceAsync].
     * @param scale The scale which one wants to convert from.
     * @param value The value which one wants to convert.
     * @param gender The gender of the scale and value to be converted.
     * @param options A map with options to configure the request.
     * @return A Deferred that will be completed with the native value.
     */
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

    /**
     * Converts multiple size values to the corresponding native size.
     * The available scales, genders and sizes can be obtained with the method [getPriceAsync].
     *
     * @param scales A list of scales to convert from.
     * @param values A list of values to convert.
     * @param genders A list of genders corresponding to the values.
     * @return A Deferred that will be completed with a list with the native values as an argument.
     */
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
