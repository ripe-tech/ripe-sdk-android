package com.ripe.android.api

import kotlinx.coroutines.Deferred

interface LocaleAPI : BaseAPI {
    fun localeAsync(value: String, locale: String, options: Map<String, Any>): Deferred<Map<String, Any>?> {
        return this.localeMultipleAsync(listOf(value), locale, options)
    }

    fun localeMultipleAsync(values: List<String>, locale: String, options: Map<String, Any>): Deferred<Map<String, Any>?> {
        var url = this.getUrl() + "locale"
        var _options = mutableMapOf(
                "url" to url,
                "method" to "GET",
                "params" to mapOf(
                        "values" to values,
                        "locale" to locale
                )
        )
        _options.putAll(options)
        _options = this.build(_options).toMutableMap()
        url = _options["url"] as String
        return this.cacheURLAsync(url, _options)
    }
}
