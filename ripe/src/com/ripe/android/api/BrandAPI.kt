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

    fun getLogoOptions(options: Map<String, Any> = HashMap()): Map<String, Any>{
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

    fun getLogoUrl(options: Map<String, Any> = HashMap()): String {
        val options = this.getLogoOptions(options)
        val url = options["url"] as String
        val params = options["params"] as Map<String, Any>
        return "${url}?${this.buildQuery(params)}"
    }
    fun getMeshAsync(options: Map<String, Any> = HashMap()): Deferred<Map<String, Any>?> {
        var meshOptions = this.getMeshOptions(options)
        meshOptions = this.build(meshOptions)
        val url = meshOptions["url"] as String
        return this.cacheURLAsync(url, meshOptions)
    }

    fun getMeshOptions(options: Map<String, Any> = HashMap()): Map<String, Any>{
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
        var configOptions = this.getConfigOptions(options)
        configOptions = this.build(configOptions)
        val url = configOptions["url"] as String
        return this.cacheURLAsync(url, configOptions)
    }

    /**
     * @suppress
     */
    fun getConfigOptions(options: Map<String, Any> = HashMap()): Map<String, Any> {
        val brand = options["brand"] as String? ?: this.owner.brand
        val model = options["model"] as String? ?: this.owner.model
        val country = options["country"] as String? ?: this.owner.country
        val flag = options["flag"] as String? ?: this.owner.flag
        val filter = options["filter"] as? Boolean
        val url = "${this.getUrl()}brands/${brand}/models/${model}/config"
        val params = HashMap<String, Any>()
        if (country != null) params["country"] = country
        if (flag != null) params["flag"] = flag
        if (filter != null) params["filter"] = if (filter) "1" else "0"
        val configOptions = options.toMutableMap()
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
        var defaultOptions = this.getDefaultsOptions(options)
        defaultOptions = this.build(defaultOptions)
        val url = defaultOptions["url"] as String
        return this.cacheURLAsync(url, defaultOptions)
    }

    /**
     * @suppress
     */
    fun getDefaultsOptions(options: Map<String, Any> = HashMap()): Map<String, Any> {
        val brand = options["brand"] as String? ?: this.owner.brand
        val model = options["model"] as String? ?: this.owner.model
        val url = "${this.getUrl()}brands/${brand}/models/${model}/defaults"
        val defaultOptions = options.toMutableMap()
        defaultOptions.putAll(mapOf("url" to url, "method" to "GET"))
        return defaultOptions
    }
    fun getCombinationsOptions(options: Map<String, Any> = HashMap()): Map<String, Any> {
        val brand = options["brand"] ?: this.owner.brand
        val model = options["model"] ?: this.owner.model
        val version = options["version"] ?: this.owner.version
        val useName = options["useName"] ?: false
        val country = options["country"] ?: this.owner.country
        val flag = options["flag"] ?: this.owner.flag
        val url = "${this.getUrl()}brands/${brand}/models/${model}/combinations"
        val params = HashMap<String, Any>()
        if (version != null) params["version"] = version
        if (useName != null) params["useName"] = useName
        if (country != null) params["country"] = country
        if (flag != null) params["flag"] = flag
        if (options["resolve"] != null){
            val resolve = options["resolve"] as Boolean
            params["resolve"] = if (resolve) "1" else "0"
        }
        if (options["sort"] != null){
            val sort = options["sort"] as Boolean
            params["sort"] = if (sort) "1" else "0"
        }
        if (options["filter"] != null){
            val filter = options["filter"] as Boolean
            params["filter"] = if (filter) "1" else "0"
        }
        val combinationsOptions = options.toMutableMap()
        combinationsOptions.putAll(mapOf(
            "url" to url,
            "method" to "GET",
            "params" to params
        ))
        return combinationsOptions
    }
}
