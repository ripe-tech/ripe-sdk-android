package com.ripe.android.api

import java.net.URLEncoder
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlin.collections.HashMap

import com.ripe.android.base.Ripe
import com.ripe.android.util.DownloadURLDelegate
import com.ripe.android.util.DownloadURLTask

interface BaseAPI {
    val owner: Ripe

    fun getUrl(): String {
        return this.owner.options["url"] as String? ?: "https://sandbox.platforme.com/api/"
    }

    fun getPrice(options: HashMap<String, Any> = HashMap(), callback: (result: HashMap<String, Any>?, isValid: Boolean) -> Unit) {
        var _options = this._getPriceOptions(options)
        _options = this._build(_options)
        val url = _options["url"] as String
        this._cacheURL(url, options, callback)
    }

    fun _cacheURL(url: String, options: HashMap<String, Any>, callback: (result: HashMap<String, Any>?, isValid: Boolean) -> Unit) {
        return this._requestURL(url, options, callback)
    }

    fun _requestURL(url: String, options: HashMap<String, Any>, callback: (result: HashMap<String, Any>?, isValid: Boolean) -> Unit) {
        var url = url
        val method = options["method"] as String? ?: "GET"
        val params = options["params"] as HashMap<String, Any>? ?: HashMap()
        val headers = options["headers"] as HashMap<String, String>? ?: HashMap()
        var data = options["data"]
        var contentType = options["contentType"]

        val query = this._buildQuery(params)
        val isEmpty = arrayOf("GET", "DELETE").contains(method)
        val hasQuery = url.contains("?")
        val separator = if (hasQuery) "&" else "?"

        if (isEmpty || data != null) {
            url += separator + query
        } else {
            data = query
            contentType = "application/x-www-form-urlencoded"
        }

        val task = DownloadURLTask(object: DownloadURLDelegate {
            override fun downloadURLResult(result: String) {
                try {
                    val gson = Gson()
                    val type = object : TypeToken<HashMap<String, Any>>() {}.type
                    val resultMap = gson.fromJson<HashMap<String, Any>>(result, type)
                    callback(resultMap, true)
                } catch (exception: JsonSyntaxException) {
                    callback(null, false)
                }
            }
        })
        task.execute(url)
    }

    fun _getPriceOptions(options: HashMap<String, Any>): HashMap<String, Any> {
        val url = this.getUrl() + "config/price"
        val _options = this._getQueryOptions(options)
        _options.putAll(hashMapOf(
                "url" to url,
                "method" to "GET"
        ))
        return _options
    }

    fun _getImageOptions(options: HashMap<String, Any> = HashMap()): HashMap<String, Any> {
        val _options = this._getQueryOptions(options)
        val params: MutableMap<String, Any> = _options["params"] as HashMap<String, Any>? ?: HashMap()

        val initials = options["initials"] as String?
        val profile = options["profile"] as Array<String>?
        if (initials != null) {
            params["initials"] = initials
        }
        if (profile != null) {
            params["profile"] = profile.joinToString(",")
        }

        val url = this.getUrl() + "compose"
        _options.putAll(hashMapOf(
                "url" to url,
                "method" to "GET",
                "params" to params
        ))
        return _options
    }

    fun _getImageUrl(options: HashMap<String, Any> = HashMap()): String {
        val _options = this._getImageOptions(options)
        val url = _options["url"] as String
        val params = _options["params"] as HashMap<String, Any>
        return "${url}?${this._buildQuery(params)}"
    }

    fun _getQueryOptions(options: HashMap<String, Any>): HashMap<String, Any> {
        val params = options["params"] as HashMap<String, Any>?
                ?: HashMap()
        val brand = options["brand"] as String? ?: this.owner.brand
        val model = options["model"] as String? ?: this.owner.model
        val parts = options["parts"] as HashMap<String, Any>? ?: this.owner.getParts()

        if (brand != null) {
            params["brand"] = brand
        }
        if (model != null) {
            params["model"] = model
        }

        val partsQ = ArrayList<String>()
        for (part in parts?.keys) {
            val value = parts.get(part) as Map<String, String>
            val material = value["material"]
            val color = value["color"]
            if (material == null || color == null) {
                continue
            }
            partsQ.add("${part}:${material}:${color}")
        }
        params.set("p", partsQ)

        // TODO
        options["params"] = params
        return options
    }

    fun _build(options: HashMap<String, Any>): HashMap<String, Any> {
        return options // TODO
    }

    fun _buildQuery(params: HashMap<String, Any>): String {
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
