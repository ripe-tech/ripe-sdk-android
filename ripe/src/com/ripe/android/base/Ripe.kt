package com.ripe.android.base

import java.util.ArrayList

import com.ripe.android.api.BaseAPI
import com.ripe.android.api.RipeAPI

class Ripe constructor(var brand: String?, var model: String?, var options: Map<String, Any>) : Observable by ObservableImpl(), BaseAPI by RipeAPI(options) {

    var initials = ""
    var engraving = ""
    val children = ArrayList<Interactable>()

    init {
        if (brand != null && model != null) {
            this.config(brand!!, model!!, options)
        }
    }

    fun config(brand: String, model: String, options: Map<String, Any>?) {
        this.brand = brand
        this.model = model
        this.options = options ?: this.options
    }
}
