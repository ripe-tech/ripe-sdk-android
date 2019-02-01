package com.ripe.android.api

import com.ripe.android.util.DownloadURLDelegate
import com.ripe.android.util.DownloadURLTask
import java.net.URL
import java.net.URLEncoder
import java.io.BufferedReader
import java.io.InputStreamReader
import org.json.JSONObject
import kotlin.reflect.KCallable

interface BaseAPI {
    val url: String

    fun getPrice(options: HashMap<String, Any>, callback: (args: Any?) -> Unit) {
        var _options = this._getPriceOptions(options)
        _options = this._build(_options)
        val url = _options["url"] as String
        this._cacheURL(url, options, callback)
    }

    fun _cacheURL(url: String, options: HashMap<String, Any>, callback: (args: Any?) -> Unit) {
        return this._requestURL(url, options, callback)
    }

    fun _requestURL(url: String, options: HashMap<String, Any>, callback: (args: Any?) -> Unit) {
        var url = url
        val method = options["method"] as String? ?: "GET"
        val params = options["params"] as HashMap<String, String>? ?: HashMap()
        val headers = options["headers"] as HashMap<String, String>? ?: HashMap()
        var data = options["data"]
        var contentType = options["contentType"]

        val query = this._buildQuery(params) + "&p=side:leather_dmy:black"
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
                callback(result)
            }
        })
        task.execute(url)
    }

    fun _getPriceOptions(options: HashMap<String, Any>): HashMap<String, Any> {
        val url = this.url + "config/price"
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

        val url = this.url + "compose"
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
        val params = _options["params"] as HashMap<String, String>
        return "${url}?${this._buildQuery(params)}"
    }

    fun _getQueryOptions(options: HashMap<String, Any>): HashMap<String, Any> {
        val params: HashMap<String, String> = options["params"] as HashMap<String, String>?
                ?: HashMap()
        params["brand"] = options["brand"] as String
        params["model"] = options["model"] as String

        // TODO
        options["params"] = params
        return options
    }

    fun _build(options: HashMap<String, Any>): HashMap<String, Any> {
        return options // TODO
    }

    fun _buildQuery(params: HashMap<String, String>): String {
        val buffer = ArrayList<String>()
        params.forEach { (key, value) ->
            var valueS = value.toString()
            valueS = URLEncoder.encode(valueS, "UTF-8")
            buffer.add("$key=$valueS")
        }
        return buffer.joinToString("&")
    }

    fun _cacheURL(url: String, options: HashMap<String, Any>): JSONObject {
        return this._requestURL(url, options)
    }

    fun _requestURL(url: String, options: HashMap<String, Any>): JSONObject {
        val _url = URL(url)
        val inputStream = _url.openStream()
        BufferedReader(InputStreamReader(inputStream)).use {
            val response = StringBuffer()
            var inputLine = it.readLine()
            while (inputLine != null) {
                response.append(inputLine)
                inputLine = it.readLine()
            }
            return JSONObject(response.toString())
        }
    }
}
