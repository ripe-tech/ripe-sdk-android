package com.ripe.android.base

import kotlin.collections.HashMap
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import android.widget.ImageView
import com.ripe.android.api.RipeAPI
import com.ripe.android.visual.Image

class Ripe constructor(var brand: String?, var model: String?, options: Map<String, Any> = HashMap()) : Observable() {
    var options = options.toMutableMap()
    var api = RipeAPI(this)
    var initials = ""
    var engraving = ""
    var children: MutableList<Interactable> = ArrayList()
    var loadedConfig: Map<String, Any>? = null
    var ready = false
    var useDefaults = true
    var usePrice = true

    private var parts: MutableMap<String, Any> = HashMap()

    init {
        this._setOptions(options)

        val ripe = this

        @Suppress("experimental_api_usage")
        MainScope().launch {
            ripe.config(brand, model, options)
        }
    }

    suspend fun config(brand: String?, model: String?, options: Map<String, Any> = HashMap()) {
        // triggers the 'pre_config' event so that
        // the listeners can cleanup if needed
        this.trigger("pre_config").await()

        // updates the current references to both the brand
        // and the model according to the new configuration
        // request (update before remote update)
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

        // determines if a valid model is currently defined for the ripe
        // instance, as this is going to change some logic behaviour
        val hasModel = this.brand != null && this.model != null

        // retrieves the configuration for the currently loaded model so
        // that others may use it freely (cache mechanism)
        this.loadedConfig = if (hasModel) this.api.getConfigAsync().await() else { null }

        // determines if the defaults for the selected model should
        // be loaded so that the parts structure is initially populated
        val hasParts = this.parts.isNotEmpty()
        val loadDefaults = !hasParts && this.useDefaults && hasModel

        // in case the current instance already contains configured parts
        // the instance is marked as ready (for complex resolution like price)
        // for cases where this is the first configuration (not an update)
        val update = this.options["update"] as Boolean? ?: false
        this.ready = if (update) this.ready else hasParts

        // triggers the config event notifying any listener that the (base)
        // configuration for this main RIPE instance has changed
        this.trigger("config", this.loadedConfig ?: HashMap()).await()

        // determines the proper initial parts for the model taking into account
        // if the defaults should be loaded
        var parts = this.parts
        if (loadDefaults) {
            @Suppress("unchecked_cast")
            val defaults = this.loadedConfig!!["defaults"] as Map<String, Any>
            parts = defaults.toMutableMap()
        }
        if (!this.ready) {
            this.ready = true
            this.trigger("ready")
        }

        // in case there's no model defined in the current instance then there's
        // nothing more possible to be done, returns the control flow
        if (!hasModel) {
            this.trigger("post_config").await()
            return
        }

        // updates the parts of the current instance
        this.setParts(parts, false, mapOf("noPartEvents" to true))

        // notifies that the config has changed and waits for listeners
        // before concluding the config operation
        this.trigger("post_config").await()

        // triggers the local update operations
        this.update()
    }

    fun update(state: Map<String, Any> = this._getState()) {
        this.children.forEach { it.update(state) }

        if (this.ready) this.trigger("update")

        if(this.ready && this.usePrice) {
            val ripe = this
            @Suppress("experimental_api_usage")
            MainScope().launch {
                val price = ripe.api.getPriceAsync().await()
                if (price != null) {
                    ripe.trigger("price", price)
                }
            }
        }
    }

    fun getParts(): Map<String, Any> {
        return this.parts
    }

    fun setPart(part: String, material: String?, color: String?, noEvents: Boolean = false, options: Map<String, Any> = HashMap()) {
        if (noEvents) {
            return this._setPart(part, material, color)
        }

        val eventValue = mapOf<String, Any>("parts" to this.parts, "options" to options)
        this.trigger("pre_parts", eventValue)
        this._setPart(part, material, color)
        this.update()
        this.trigger("parts", eventValue)
        this.trigger("post_parts", eventValue)
    }

    fun setParts(parts: Any, noEvents: Boolean = false, options: Map<String, Any> = HashMap()) {
        @Suppress("unchecked_cast")
        var partsList = parts as? ArrayList<ArrayList<String?>> ?: ArrayList()
        if (parts is HashMap<*, *>) {
            @Suppress("unchecked_cast")
            partsList = this._partsList(parts as HashMap<String, Any>)
        }

        val noPartEvent = options["noPartEvent"] as? Boolean ?: false

        if (noEvents) {
            return this.setParts(partsList, noPartEvent)
        }

        val eventValue = mapOf("parts" to this.parts, "options" to options)
        this.trigger("pre_parts", eventValue)
        this._setParts(partsList, noPartEvent)
        this.update()
        this.trigger("parts", eventValue)
        this.trigger("post_parts", eventValue)
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

    fun _getState(): Map<String, Any> {
        return mapOf(
                "parts" to this.parts,
                "initials"  to this.initials,
                "engraving" to this.engraving
        )
    }

    fun _setOptions(options: Map<String, Any>) {
        this.options = options.toMutableMap()
        @Suppress("unchecked_cast")
        val parts = options["parts"] as? MutableMap<String, Any>
        val useDefaults = options["useDefaults"] as? Boolean
        val usePrice = options["usePrice"] as? Boolean

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

    fun _setPart(part: String, material: String?, color: String?, noEvents: Boolean = false) {
        // ensures that there's one valid configuration loaded
        // in the current instance, required for part setting
        if (this.loadedConfig == null) {
            throw Error("Model config is not loaded")
        }

        // if the material or color are not set then this
        // is considered a removal operation and the part
        // is removed from the parts structure if it's
        // optional or an error is thrown if it's required
        @Suppress("unchecked_cast")
        val defaults = this.loadedConfig!!["defaults"] as Map<String, Any>
        @Suppress("unchecked_cast")
        val partInfo = defaults[part] as Map<String, Any>
        val isOptional = partInfo["optional"] as? Boolean ?: false
        val isRequired = !isOptional
        val remove = material == null && color == null
        if (isRequired && remove) {
            throw Error("Part ${part} can't be removed")
        }

        @Suppress("unchecked_cast")
        val value: MutableMap<String, String?> = this.parts[part] as? MutableMap<String, String?> ?: HashMap()
        value["material"] = if (remove) null else material
        value["color"] = if (remove) null else color

        if (noEvents) {
            if (remove) {
                this.parts.remove(part)
            } else {
                this.parts[part] = value
            }
        }

        val eventValue = mapOf("part" to part, "value" to value)
        this.trigger("pre_part", eventValue)
        if (remove) {
            this.parts.remove(part)
        } else {
            this.parts[part] = value
        }
        this.trigger("part", eventValue)
        this.trigger("post_part", eventValue)
    }

    fun _setParts(update: List<List<String?>>, noEvents: Boolean) {
        update.forEach { this._setPart(it[0]!!, it[1], it[2], noEvents) }
    }

    fun _partsList(parts: HashMap<String, Any>): ArrayList<ArrayList<String?>> {
        val partsList = ArrayList<ArrayList<String?>>()
        for ((key, value) in parts) {
            @Suppress("unchecked_cast")
            val part = value as Map<String, String>
            partsList.add(arrayListOf(key, part["material"], part["color"]))
        }
        return partsList
    }
}
