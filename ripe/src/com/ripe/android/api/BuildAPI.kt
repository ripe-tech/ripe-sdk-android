package com.ripe.android.api

import kotlinx.coroutines.Deferred

/**
 * The interface for the Build API.
 */
interface BuildAPI : BaseAPI {
    /**
     * Retrieves the bundle of part, materials and colors translations of a specific brand and model
     * If no model is defined the retrieves the bundle of the owner's current model.
     * The **options** map accepts the following keys:
     * - **brand** - The brand of the model.
     * - **model** - The name of the model.
     * - **locale** - The locale of the translations.
     * - **compatibility** - If compatibility mode should be enabled.
     * - **prefix** - A prefix to prepend to the locale keys (defaults to `builds`).
     *
     * @param options A map of options to configure the request.
     * @return A Deferred that will be completed with the locale bundle.
     */
    fun getLocaleModelAsync(options: Map<String, Any> = HashMap()): Deferred<Map<String, Any>?> {
        var localeOptions = this._getLocaleModelOptions(options)
        localeOptions = this.build(localeOptions)
        val url = localeOptions["url"] as String
        return this.cacheURLAsync(url, localeOptions)
    }

    /**
     * @suppress
     */
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
