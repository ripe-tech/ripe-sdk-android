package com.ripe.android.base

import android.widget.ImageView
import com.google.gson.internal.LinkedTreeMap

import com.ripe.android.api.RipeAPI
import com.ripe.android.visual.Image
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.timerTask

class Ripe constructor(var brand: String?, var model: String?, var options: HashMap<String, Any>) : Observable() {

    var api = RipeAPI(this)
    var initials = ""
    var engraving = ""
    var children = ArrayList<Interactable>()
    var ready = false
    var useDefaults = true
    var usePrice = true

    var parts = HashMap<String, Any>()
    set(value) {
        field = value
        this.update()
    }

    init {
        if (brand != null && model != null) {
            this.config(brand!!, model!!, options)
        }
    }

    fun config(brand: String, model: String, options: HashMap<String, Any> = HashMap()) {
        this.brand = brand
        this.model = model

        // sets the new options using the current options
        // as default values and sets the update flag to
        // true if it is not set
        val _options = this.options
        _options.putAll(options)
        if (!_options.containsKey("update")) {
            _options["update"] = true
        }
        this._setOptions(_options)

        // determines if the defaults for the selected model should
        // be loaded so that the parts structure is initially populated
        val hasParts = this.parts?.isNotEmpty()
        val hasModel = this.brand != null && this.model != null
        val loadDefaults = !hasParts && this.useDefaults && hasModel
        val callback = { result: HashMap<String, Any>?, _: Boolean ->
            val parts = result ?: this.parts
            if (!this.ready) {
                this.ready = true
                this.trigger("ready")
            }
            if (hasModel) {
                val parts = parts["parts"] as LinkedTreeMap<String, Any>
                this.parts = parts.toMap<String, Any>() as HashMap<String, Any>
                this.update()
            }
        }
        if (loadDefaults) {
            this.api.getDefaults(callback = callback)
        } else {
            Timer().schedule(timerTask { callback(null, true) }, 0)
        }

        // in case the current instance already contains configured parts
        // the instance is marked as ready (for complex resolution like price)
        // for cases where this is the first configuration (not an update)
        val update = this.options["update"] as Boolean? ?: false
        this.ready = if (update) this.ready else hasParts
        this.trigger("config")
    }

    fun update(state: HashMap<String, Any> = this._getState()) {
        this.children.forEach { it.update(state) }

        if (this.ready) this.trigger("update")

        if(this.ready && this.usePrice) {
            this.api.getPrice { result, isValid ->
                if (isValid) this.trigger("price", result!!)
            }
        }
    }

    fun _setOptions(options: HashMap<String, Any>?) {
        this.options = options ?: HashMap<String, Any>()
        val parts = options?.get("parts") as HashMap<String, Any>?
        val useDefaults = options?.get("useDefaults") as Boolean?
        val usePrice = options?.get("usePrice") as Boolean?

        if (parts != null) {
            this.parts = parts
        }
        if (useDefaults != null) {
            this.useDefaults = useDefaults
        }
        if (usePrice != null) {
            this.usePrice = usePrice
        }
    }

    fun setInitials(initials: String, engraving: String, noUpdate: Boolean = false) {
        this.initials = initials
        this.engraving = engraving

        if (noUpdate) {
            return
        }

        this.update()
    }

    fun bindImage(view: ImageView, options: Map<String, Any> = HashMap()): Image {
        val image = Image(view, this, options)
        return this.bindInteractable(image) as Image
    }

    fun bindInteractable(child: Interactable): Interactable {
        this.children.add(child)
        return child
    }

    fun _getState(): HashMap<String, Any> {
        return hashMapOf(
                "parts" to this.parts,
                "initials"  to this.initials,
                "engraving" to this.engraving
        )
    }
}
