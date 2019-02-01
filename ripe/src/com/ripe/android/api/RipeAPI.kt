package com.ripe.android.api

interface RipeAPI: LocaleAPI

class RipeAPIImpl constructor(var options: Map<String, Any>) : RipeAPI,
        LocaleAPI by LocaleAPIImpl(options["url"] as String)
