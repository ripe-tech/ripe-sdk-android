package com.ripe.android.api

import com.ripe.android.base.Ripe

class RipeAPI constructor(override var owner: Ripe) :
        BaseAPI,
        LocaleAPI by LocaleAPIImpl(owner) {

    constructor(options: Map<String, Any>) : this(Ripe(null, null, options))
}
