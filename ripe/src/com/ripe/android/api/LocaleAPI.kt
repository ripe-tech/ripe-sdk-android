package com.ripe.android.api

import kotlinx.coroutines.Deferred

/**
 * The interface for the Locale API.
 */
interface LocaleAPI : BaseAPI {
    /**
     * Localizes a value to the provided locale.
     *
     * @param value The value to be localized.
     * @param locale The locale to localize the value to.
     * @param options A options map that configures the request.
     * @return A Deferred that will be completed with the localized value.
     */
    fun localeAsync(value: String, locale: String, options: Map<String, Any>): Deferred<Map<String, Any>?> {
        return this.localeMultipleAsync(listOf(value), locale, options)
    }

    /**
     * Localizes a list of values to the provided locale.
     *
     * @param values The values to be localized.
     * @param locale The locale to localize the values to.
     * @param options A options map that configures the request.
     * @return A Deferred that will be completed with the localized values.
     */
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
