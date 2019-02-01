package com.ripe.android.api

import java.net.URL
import java.net.URLEncoder
import java.io.BufferedReader
import java.io.InputStreamReader
import org.json.JSONObject

interface BaseAPI {
    val url: String

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
