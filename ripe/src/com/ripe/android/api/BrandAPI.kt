package com.ripe.android.api

import org.json.JSONException
import org.json.JSONObject

interface BrandAPI: BaseAPI {

    fun getDefaults(options: HashMap<String, Any> = HashMap(), callback: (defaults: HashMap<String, Any>?, isValid: Boolean) -> Unit) {
        var _options = this.getDefaultsOptions(options)
        _options = this._build(_options)
        val url = _options["url"] as String
        return this._cacheURL(url, _options) { result, isValid ->
            if (result == null || !isValid) {
                callback(null,false)
                return@_cacheURL
            }
            try {
                val defaults = HashMap<String, Any>()
                result.getJSONObject("parts")
                val parts = result.getJSONObject("parts")
                for (key in parts.keys()) {
                    val value = parts.getJSONObject(key)
                    defaults[key] = hashMapOf(
                            "material" to value.getString("material"),
                            "color" to value.getString("color")
                    )
                }
                callback(defaults, isValid)
            }
            catch (exception: JSONException) {
                callback(null, false)
            }
        }
    }

    fun getDefaultsOptions(options: HashMap<String, Any> = HashMap()): HashMap<String, Any> {
        val brand = options["brand"] as String? ?: this.owner.brand
        val model = options["model"] as String? ?: this.owner.model
        val url = "${this.getUrl()}brands/${brand}/models/${model}/defaults"
        options.putAll(hashMapOf("url" to url, "method" to "GET"))
        return options
    }
}

