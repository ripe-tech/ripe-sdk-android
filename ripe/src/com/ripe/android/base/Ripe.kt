package com.ripe.android.base

import android.widget.ImageView
import java.util.ArrayList

import com.ripe.android.api.RipeAPI
import com.ripe.android.visual.Image

class Ripe constructor(var brand: String?, var model: String?, var options: Map<String, Any>) : Observable() {

    var api = RipeAPI(this)
    var parts = HashMap<String, Any>()
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

    fun update(state: HashMap<String, Any> = this._getState()) {
        this.children.forEach { it.update(state) }
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
