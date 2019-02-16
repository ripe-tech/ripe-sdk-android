package com.ripe.android.api

interface BrandAPI: BaseAPI {

    fun getDefaults(options: HashMap<String, Any> = HashMap(), callback: (defaults: HashMap<String, Any>?, isValid: Boolean) -> Unit) {
        var _options = this.getDefaultsOptions(options)
        _options = this._build(_options)
        val url = _options["url"] as String
        return this._cacheURL(url, _options, callback)
    }

    fun getDefaultsOptions(options: HashMap<String, Any> = HashMap()): HashMap<String, Any> {
        val brand = options["brand"] as String? ?: this.owner.brand
        val model = options["model"] as String? ?: this.owner.model
        val url = "${this.getUrl()}brands/${brand}/models/${model}/defaults"
        options.putAll(hashMapOf("url" to url, "method" to "GET"))
        return options
    }
}
