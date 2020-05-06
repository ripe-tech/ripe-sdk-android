package com.ripe.android.api

import com.ripe.android.base.Ripe

/**
 * The API class to be instantiated. Implements all the API interfaces.
 *
 * @property owner The Ripe instance that will use this API.
 * @constructor Builds a RipeAPI instance with the provided Ripe instance as owner.
 */
class RipeAPI constructor(override var owner: Ripe) :
        BaseAPI,
        BrandAPI,
        BuildAPI,
        LocaleAPI,
        SizeAPI {

    /**
     * Builds a standalone API instance.
     */
    constructor(options: Map<String, Any>) : this(Ripe(null, null, options))
}
