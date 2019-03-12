package com.ripe.android.api

import kotlinx.coroutines.Deferred

interface BuildAPI : BaseAPI {
    fun getLocaleModelAsync(options: Map<String, Any> = HashMap()): Deferred<Map<String, Any>?> {
        var localeOptions = this._getLocaleModelOptions(options)
        localeOptions = this.build(localeOptions)
        val url = localeOptions["url"] as String
        return this.cacheURLAsync(url, localeOptions)
    }

    private fun _getLocaleModelOptions(options: Map<String, Any>): Map<String, Any> {
        val brand = options["brand"] ?: this.owner.brand
        val model = options["model"] ?: this.owner.model
        val locale = options["locale"] ?: this.owner.locale
        val url = "${this.getUrl()}builds/$brand/locale/$locale"
        val params = HashMap<String, Any>()
        if (model != null) {
            params["model"] = model
        }
        if (options["compatibility"] != null) {
            val compatibility = options["compatibility"] as Boolean
            params["compatibility"] = if (compatibility) "1" else "0"
        }
        if (options["prefix"] != null) {
            val prefix = options["prefix"] as String
            params["prefix"] = prefix
        }

        val localeOptions = options.toMutableMap()
        localeOptions.putAll(mapOf(
                "url" to url,
                "method" to "GET",
                "params" to params
        ))
        return localeOptions
    }
}
