package com.ripe.android.api

class RipeAPI constructor(var options: Map<String, Any>) : BaseAPI {
    override val url: String = options["url"] as String
}
