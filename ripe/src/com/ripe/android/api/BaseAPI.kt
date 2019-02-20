package com.ripe.android.api

import java.net.URLEncoder
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

import com.ripe.android.base.Ripe
import com.ripe.android.util.DownloadURLDelegate
import com.ripe.android.util.DownloadURLTask

interface BaseAPI {
    val owner: Ripe

    fun getUrl(): String {
        return this.owner.options["url"] as String? ?: "https://sandbox.platforme.com/api/"
    }

    fun getPrice(options: Map<String, Any> = HashMap(), callback: (result: Map<String, Any>?, isValid: Boolean) -> Unit) {
        var priceOptions = this.getPriceOptions(options)
        priceOptions = this.build(priceOptions)
        val url = priceOptions["url"] as String
        this.cacheURL(url, priceOptions, callback)
    }

    fun cacheURL(url: String, options: Map<String, Any>, callback: (result: Map<String, Any>?, isValid: Boolean) -> Unit) {
        return this.requestURL(url, options, callback)
    }

    fun requestURL(url: String, options: Map<String, Any>, callback: (result: Map<String, Any>?, isValid: Boolean) -> Unit) {
        var requestUrl = url
        val method = options["method"] as String? ?: "GET"
        @Suppress("unchecked_cast")
        val params = options["params"] as Map<String, Any>? ?: HashMap()
        @Suppress("unchecked_cast")
        val headers = options["headers"] as Map<String, String>? ?: HashMap()
        var data = options["data"]
        var contentType = options["contentType"]

        val query = this.buildQuery(params)
        val isEmpty = arrayOf("GET", "DELETE").contains(method)
        val hasQuery = requestUrl.contains("?")
        val separator = if (hasQuery) "&" else "?"

        if (isEmpty || data != null) {
            requestUrl += separator + query
        } else {
            data = query
            contentType = "application/x-www-form-urlencoded"
        }

        val task = DownloadURLTask(object: DownloadURLDelegate {
            override fun downloadURLResult(result: String) {
                try {
                    val gson = Gson()
                    val type = object : TypeToken<Map<String, Any>>() {}.type
                    val resultMap = gson.fromJson<Map<String, Any>>(result, type)
                    callback(resultMap, true)
                } catch (exception: JsonSyntaxException) {
                    callback(null, false)
                }
            }
        })
        task.execute(requestUrl)
    }

    fun getPriceOptions(options: Map<String, Any>): Map<String, Any> {
        val url = this.getUrl() + "config/price"
        val result = this.getQueryOptions(options).toMutableMap()
        result.putAll(mapOf(
                "url" to url,
                "method" to "GET"
        ))
        return result
    }

    fun getImageOptions(options: Map<String, Any> = HashMap()): Map<String, Any> {
        val result = this.getQueryOptions(options).toMutableMap()
        @Suppress("unchecked_cast")
        val params: MutableMap<String, Any> = result["params"] as? MutableMap<String, Any> ?: HashMap()

        val initials = options["initials"] as String?
        @Suppress("unchecked_cast")
        val profile = options["profile"] as Array<String>?
        if (initials != null) {
            params["initials"] = initials
        }
        if (profile != null) {
            params["profile"] = profile.joinToString(",")
        }

        val url = this.getUrl() + "compose"
        result.putAll(mapOf(
                "url" to url,
                "method" to "GET",
                "params" to params
        ))
        return result
    }

    fun getImageUrl(options: Map<String, Any> = HashMap()): String {
        val imageOptions = this.getImageOptions(options)
        val url = imageOptions["url"] as String
        @Suppress("unchecked_cast")
        val params = imageOptions["params"] as Map<String, Any>
        return "${url}?${this.buildQuery(params)}"
    }

    fun getQueryOptions(options: Map<String, Any>): Map<String, Any> {
        val result = options.toMutableMap()
        @Suppress("unchecked_cast")
        val optionsParams = options["params"] as Map<String, Any>?
                ?: HashMap()
        val brand = options["brand"] as String? ?: this.owner.brand
        val model = options["model"] as String? ?: this.owner.model
        @Suppress("unchecked_cast")
        val parts = options["parts"] as? Map<String, Any> ?: this.owner.getParts()

        val params = optionsParams.toMutableMap()
        if (brand != null) {
            params["brand"] = brand
        }
        if (model != null) {
            params["model"] = model
        }

        val partsQ = ArrayList<String>()
        for (part in parts.keys) {
            @Suppress("unchecked_cast")
            val value = parts[part] as Map<String, String>
            val material = value["material"]
            val color = value["color"]
            if (material == null || color == null) {
                continue
            }
            partsQ.add("${part}:${material}:${color}")
        }
        params["p"] = partsQ

        // TODO
        result["params"] = params
        return result
    }

    fun build(options: Map<String, Any>): Map<String, Any> {
        return options // TODO
    }

    fun buildQuery(params: Map<String, Any>): String {
        val buffer = ArrayList<String>()
        params.forEach { (key, value) ->
            if (value is String) {
                val valueS = URLEncoder.encode(value, "UTF-8")
                buffer.add("$key=$valueS")
            } else if (value is ArrayList<*>) {
                value.forEach {
                    val valueS = URLEncoder.encode(it.toString(),"UTF-8")
                    buffer.add("$key=$valueS")
                }
            }
        }

        return buffer.joinToString("&")
    }
}
