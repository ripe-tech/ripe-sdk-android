package com.ripe.android.base

import android.widget.ImageView

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
    var useDefaults = true

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
        this.options.putAll(options)

        this._setOptions(options)

        val hasParts = this.parts?.isNotEmpty()
        val hasModel = this.brand != null && this.model != null
        val loadDefaults = !hasParts && this.useDefaults && hasModel

        val callback: (result: HashMap<String, Any>?, isValid: Boolean) -> Unit = { result, _ ->
            val parts = result ?: this.parts
            this.parts = parts
            this.update()
        }

        if (loadDefaults) {
            this.api.getDefaults(callback = callback)
        } else {
            Timer().schedule(timerTask { callback(null, true) }, 1000)
        }
    }

    fun update(state: HashMap<String, Any> = this._getState()) {
        this.children.forEach { it.update(state) }
    }

    fun _setOptions(options: Map<String, Any>?) {
        val parts = options?.get("parts") as HashMap<String, Any>?
        val useDefaults = options?.get("useDefaults") as Boolean?

        if (parts != null) {
            this.parts = parts
        }
        if (useDefaults != null) {
            this.useDefaults = useDefaults
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
