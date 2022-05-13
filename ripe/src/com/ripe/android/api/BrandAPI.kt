package com.ripe.android.api

import kotlinx.coroutines.Deferred

/**
 * The interface for the Brand API.
 */
interface BrandAPI : BaseAPI {
    fun getLogoAsync(options: Map<String, Any> = HashMap()): Deferred<Map<String, Any>?> {
        var logoOptions = this.getLogoOptions(options)
        logoOptions = this.build(logoOptions)
        val url = logoOptions["url"] as String
        return this.cacheURLAsync(url, logoOptions)
    }

    private fun getLogoOptions(options: Map<String, Any> = HashMap()): Map<String, Any>{
        val brand = options["brand"] ?: this.owner.brand
        val version = options["version"] ?: this.owner.version
        val variant = options["variant"] ?: this.owner.variant
        val format = options["format"] ?: "png"
        val url = "${this.getUrl()}brands/${brand}/logo.${format}"
        val params = HashMap<String, Any>()
        if (version != null) params["version"] = version
        if (variant != null) params["variant"] = variant
        val logoOptions = options.toMutableMap()
        logoOptions.putAll(mapOf("url" to url, "method" to "GET", "params" to params))
        return logoOptions
    }

    fun getMeshAsync(options: Map<String, Any> = HashMap()): Deferred<Map<String, Any>?> {
        var meshOptions = this.getMeshOptions(options)
        meshOptions = this.build(meshOptions)
        val url = meshOptions["url"] as String
        return this.cacheURLAsync(url, meshOptions)
    }

    private fun getMeshOptions(options: Map<String, Any> = HashMap()): Map<String, Any>{
        val brand = options["brand"] ?: this.owner.brand
        val model = options["model"] ?: this.owner.model
        val version = options["version"] ?: this.owner.version
        val variant = options["variant"] ?: this.owner.variant
        val url = "${this.getUrl()}brands/${brand}/models/${model}/mesh"
        val params = HashMap<String, Any>()
        if (version != null) params["version"] = version
        if (variant != null) params["variant"] = variant
        val meshOptions = options.toMutableMap()
        meshOptions.putAll(mapOf("url" to url, "method" to "GET", "params" to params))
        return meshOptions
    }

    /**
     * Returns the configuration information of a specific brand and model. If no model is provided
     * then returns the information of the owner's current model.
     * The **options** map accepts the following keys:
     *  - **brand** - the brand of the model
     *  - **model** - the name of the model
     *  - **country** - the country where the model will be provided, some materials/colors might not be available.
     *  - **flag** - a specific flag that may change the provided materials/colors available.
     *  - **filter** - if the configuration should be filtered by the country and/or flag. `true` by default.
     *
     * @param options A map with options.
     * @return A Deferred that will be completed with the result.
     */
    fun getConfigAsync(options: Map<String, Any> = HashMap()): Deferred<Map<String, Any>?> {
        var _options = this.getConfigOptions(options)
        _options = this.build(_options)
        val url = _options["url"] as String
        return this.cacheURLAsync(url, _options)
    }

    /**
     * @suppress
     */
    private fun getConfigOptions(options: Map<String, Any> = HashMap()): Map<String, Any> {
        val configOptions = options.toMutableMap()
        val brand = options["brand"] as String? ?: this.owner.brand
        val model = options["model"] as String? ?: this.owner.model
        val country = options["country"] as String? ?: this.owner.country
        val flag = options["flag"] as String? ?: this.owner.flag
        val filter = options["filter"] as? Boolean
        val url = "${this.getUrl()}brands/${brand}/models/${model}/config"

        val params = HashMap<String, Any>()
        if (country != null) {
            params["country"] = country
        }
        if (flag != null) {
            params["flag"] = flag
        }

        if (filter != null) {
            params["filter"] = if (filter) "1" else "0"
        }

        configOptions.putAll(mapOf("url" to url, "method" to "GET", "params" to params))
        return configOptions
    }

    /**
     * Returns the default customization of a specific brand or model. If no model is provided
     * then returns the defaults of the owner's current model.
     *
     * @param options a map that accepts *brand* and *model* as keys.
     * @return A Deferred that will be completed with the result.
     */
    fun getDefaultsAsync(options: Map<String, Any> = HashMap()): Deferred<Map<String, Any>?> {
        var _options = this.getDefaultsOptions(options)
        _options = this.build(_options)
        val url = _options["url"] as String
        return this.cacheURLAsync(url, _options)
    }

    /**
     * @suppress
     */
    private fun getDefaultsOptions(options: Map<String, Any> = HashMap()): Map<String, Any> {
        val defaultOptions = options.toMutableMap()
        val brand = options["brand"] as String? ?: this.owner.brand
        val model = options["model"] as String? ?: this.owner.model
        val url = "${this.getUrl()}brands/${brand}/models/${model}/defaults"
        defaultOptions.putAll(mapOf("url" to url, "method" to "GET"))
        return defaultOptions
    }
}
