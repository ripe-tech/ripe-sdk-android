package com.ripe.android.base

import android.widget.ImageView
import com.ripe.android.api.RipeAPI
import com.ripe.android.plugins.Plugin
import com.ripe.android.visual.Image
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Represents a customizable model.
 * The **options** map supports the following keys:
 *  - **variant** - The variant of the model.
 *  - **parts** - The initial parts of the model.
 *  - **country** - The country where the model will be sold.
 *  - **currency** - The currency that should be used to calculate the price.
 *  - **locale** - The locale to be used by default when localizing values.
 *  - **flag** - A specific attribute of the model.
 *  - **useDefaults** - If the default parts of the model should be used when no initials parts are set.
 *  - **usePrice** - If the price should be automatically retrieved whenever there is a customization change.
 *  - **plugins** - A list of plugins to be registered to the Ripe instance.
 *
 * @property brand The brand of the model.
 * @property model The name of the model.
 * @param options A map with options to configure the Ripe instance.
 * @constructor Creates a Ripe instance with the provided brand, model and options.
 */
class Ripe @JvmOverloads constructor(var brand: String?, var model: String?, options: Map<String, Any> = HashMap()) : Observable() {
    /**
     * A map with options to customise the Ripe instance.
     */
    var options = options.toMutableMap()

    /**
     * The variant of the model.
     */
    var variant: String? = null

    /**
     * The API instance to be used.
     */
    var api = RipeAPI(this)

    /**
     * The current customisation of the model.
     */
    var parts: MutableMap<String, Any> = HashMap()

    /**
     * The current initials of the model.
     */
    var initials = ""

    /**
     * The current engraving of the model.
     */
    var engraving = ""

    /**
     * The configuration information of the current model.
     */
    var loadedConfig: Map<String, Any>? = null

    /**
     * If the instance is ready for interactions.
     */
    var ready = false

    /**
     * A specific attribute of the model.
     */
    var flag: String? = null

    /**
     * The country where the model will be delivered.
     */
    var country: String? = null

    /**
     * The currency to use when calculating the price.
     */
    var currency: String? = null

    /**
     * The default locale to use when localizing values.
     */
    var locale: String? = null

    private var children: MutableList<Interactable> = ArrayList()
    private var plugins: MutableList<Plugin> = ArrayList()
    private var useDefaults = true
    private var usePrice = true
    private var history: MutableList<Map<String, Any>> = ArrayList()
    private var historyPointer = -1

    init {
        this._setOptions(options)

        val ripe = this

        // iterates over all the plugins present in the options (meant
        // to be registered) and adds them to the current instance
        @Suppress("unchecked_cast")
        val plugins = options["plugins"] as? List<Plugin>
        plugins?.forEach {
            this.addPlugin(it)
        }

        // runs the configuration operation on the current instance, using
        // the requested parameters and options, multiple configuration
        // operations may be executed over the object life-time
        @Suppress("experimental_api_usage")
        MainScope().launch {
            ripe.config(brand, model, options)
        }

        // listens for the post parts event and saves the current configuration
        // for the undo operations (history control)
        this.bind("post_parts") {
            // in case the current operation was an undo and redo one there's
            // nothing to be done (no history stack change)
            @Suppress("unchecked_cast")
            val options = it["options"] as? Map<String, Any>
            val action = options?.get("action") as? String
            if (action in arrayOf("undo", "redo")) {
                return@bind
            }

            // pushes the current state of the configuration (parts) into
            // the history stack allowing undo and redo
            this._pushHistory()
        }
    }

    /**
     * Sets the model to be customised.
     *
     * The **options** map supports the following keys:
     *  - **variant** - The variant of the model.
     *  - **parts** - The initial parts of the model.
     *  - **country** - The country where the model will be sold.
     *  - **currency** - The currency that should be used to calculate the price.
     *  - **locale** - The locale to be used by default when localizing values.
     *  - **flag** - A specific attribute of the model.
     *  - **useDefaults** - If the default parts of the model should be used when no initials parts are set.
     *  - **usePrice** - If the price should be automatically retrieved whenever there is a customization change.
     *
     * @property brand The brand of the model.
     * @property model The name of the Model.
     * @param options A map with options to configure the Ripe instance.
     */
    @JvmOverloads
    suspend fun config(brand: String?, model: String?, options: Map<String, Any> = HashMap()) {
        // triggers the 'pre_config' event so that
        // the listeners can cleanup if needed
        this.trigger("pre_config").await()

        // updates the current references to both the brand
        // and the model according to the new configuration
        // request (update before remote update)
        this.brand = brand
        this.model = model

        // resets the history related values as the current
        // model has changed and no previous history is possible
        this.history = ArrayList()
        this.historyPointer = -1

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
        this.loadedConfig = if (hasModel) this.api.getConfigAsync().await() else null

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

        // updates the parts of the current instance according to either
        // the (explicitly) provided parts or the ones loaded from the config
        this.setParts(parts, false, mapOf("noPartEvents" to true))

        // notifies that the config has changed and waits for listeners
        // before concluding the config operation
        this.trigger("post_config", this.loadedConfig!!).await()

        // triggers the local update operations to flush both the visual
        // and the logic part of the ripe instance
        this.update()
    }

    /**
     * Triggers the update of the children so that they represent the current state of the model.
     *
     * @param state A map with the current customization and personalization.
     */
    @JvmOverloads
    fun update(state: Map<String, Any> = this._getState()) {
        this.children.forEach { it.update(state) }

        // in case the current instance is now ready, should trigger
        // the update event (as expected)
        if (this.ready) this.trigger("update")

        // in case the current instance is ready and the use price
        // flag is set then triggers the running of the price retrieval
        // process that should finish with the price event
        if (this.ready && this.usePrice) {
            val ripe = this
            @Suppress("experimental_api_usage")
            MainScope().launch {
                try {
                    val price = ripe.api.getPriceAsync().await()
                    if (price != null) {
                        ripe.trigger("price", price)
                    }
                } catch (exception: Exception) {
                }
            }
        }
    }

    /**
     * Retrieves a copy of the current customization.
     * @return A map with the current parts.
     */
    fun getPartsCopy(): Map<String, Any> {
        return cloneParts(this.parts)
    }

    /**
     * Changes the customization of a part.
     *
     * @param part The part to be changed.
     * @param material The material to change to.
     * @param color The color to change to.
     * @param noEvents If the parts events shouldn't be triggered (defaults `False`).
     * @param options A map with options to configure the operation (for internal use).
     */
    @JvmOverloads
    fun setPart(part: String, material: String?, color: String?, noEvents: Boolean = false, options: Map<String, Any> = HashMap()) {
        if (noEvents) {
            this._setPart(part, material, color)
        }

        val eventValue = mapOf<String, Any>("parts" to this.parts, "options" to options)
        this.trigger("pre_parts", eventValue)
        this._setPart(part, material, color)
        this.update()
        this.trigger("parts", eventValue)
        this.trigger("post_parts", eventValue)
    }

    /**
     * Allows changing the customization of a set of parts in bulk.
     *
     * @param parts A map or array with part, material, color triplets to be set.
     * @param noEvents If the parts events shouldn't be triggered (defaults `False`).
     * @param options A map with options to configure the operation (for internal use).
     */
    @JvmOverloads
    fun setParts(parts: Any, noEvents: Boolean = false, options: Map<String, Any> = HashMap()) {
        @Suppress("unchecked_cast")
        var partsList = parts as? ArrayList<ArrayList<String?>> ?: ArrayList()
        if (parts is HashMap<*, *>) {
            @Suppress("unchecked_cast")
            partsList = this._partsList(parts as HashMap<String, Any>)
        }

        val noPartEvents = options["noPartEvents"] as? Boolean ?: false

        if (noEvents) {
            return this.setParts(partsList, noPartEvents)
        }

        val eventValue = mapOf("parts" to this.parts, "options" to options)
        this.trigger("pre_parts", eventValue)
        this._setParts(partsList, noPartEvents)
        this.update()
        this.trigger("parts", eventValue)
        this.trigger("post_parts", eventValue)
    }

    /**
     * Changes the personalization of the model.
     *
     * @param initials The initials to be set.
     * @param engraving The engraving to be set.
     * @param noUpdate If the update operation shouldn't be triggered (defaults `False`).
     *
     */
    @JvmOverloads
    fun setInitials(initials: String, engraving: String, noUpdate: Boolean = false) {
        this.initials = initials
        this.engraving = engraving

        if (noUpdate) {
            return
        }

        this.update()
    }

    /**
     * Binds an [Image] to this Ripe instance.
     *
     * @param view The ImageView to be used by the Ripe instance.
     * @param options A map with options to configure the Image instance.
     * @return The [Image] instance created.
     */
    @JvmOverloads
    fun bindImage(view: ImageView, options: Map<String, Any> = HashMap()): Image {
        val image = Image(view, this, options)
        return this.bindInteractable(image) as Image
    }

    /**
     * Binds an [Interactable] to this Ripe instance.
     *
     * @param child The [Interactable] instance to be bound to the Ripe instance.
     * @return The [Interactable] instance created.
     */
    fun bindInteractable(child: Interactable): Interactable {
        this.children.add(child)
        return child
    }

    /**
     * Unbinds an [Image] from this Ripe instance.
     *
     * @param image The [Image] instance to be unbound.
     */
    fun unbindImage(image: Image) {
        this.unbindInteractable(image)
    }

    /**
     * Unbinds an [Interactable] from this Ripe instance.
     *
     * @param child The [Interactable] instance to be unbound.
     */
    fun unbindInteractable(child: Interactable) {
        child.deinit()
        this.children.remove(child)
    }

    /**
     * Selects a part of the model. Triggers a "selected_part" event with the part.
     *
     * @param part The name of the part to be selected.
     * @param options A map with options to configure the operation.
     */
    @JvmOverloads
    fun selectPart(part: String, options: Map<String, Any> = HashMap()) {
        this.trigger("selected_part", mapOf("part" to part))
    }

    /**
     * Deselects a part of the model. Triggers a "deselected_part" event with the part.
     *
     * @param part The name of the part to be selected.
     * @param options A map with options to configure the operation.
     */
    @JvmOverloads
    fun deselectPart(part: String, options: Map<String, Any> = HashMap()) {
        this.trigger("deselected_part", mapOf("part" to part))
    }

    /**
     * Reverses the last change to the parts. It is possible
     * to undo all the changes done from the initial state.
     */
    fun undo() {
        if (!this.canUndo()) {
            return
        }

        this.historyPointer -= 1
        val parts = this.history[this.historyPointer]
        this.setParts(parts, false, mapOf("action" to "undo"))
    }

    /**
     * Reapplies the last change to the parts that was undone.
     * Notice that if there's a change when the history pointer
     * is in the middle of the stack the complete stack forward
     * is removed (history re-written).
     */
    fun redo() {
        if (!this.canRedo()) {
            return
        }

        this.historyPointer += 1
        val parts = this.history[this.historyPointer]
        this.setParts(parts, false, mapOf("action" to "redo"))
    }

    /**
     * Indicates if there are part changes to undo.
     *
     * @return If there are changes to reverse in the
     * current parts history stack.
     */
    fun canUndo(): Boolean {
        return this.historyPointer > 0
    }

    /**
     * Indicates if there are part changes to redo.
     *
     * @return If there are changes to reapply pending
     * in the history stack.
     */
    fun canRedo(): Boolean {
        return this.history.size - 1 > this.historyPointer
    }

    /**
     * Verifies if the current ripe instance is ready (properly loaded)
     * and if not blocks until it becomes ready.
     *
     * @return If the current instance has successfully been initialized.
     */
    suspend fun isReady(): Boolean? {
        if (this.ready) {
            return true
        } else {
            this.waitForEvent("ready")
            return true
        }
    }

    /**
     * Registers a plugin to this Ripe instance.
     *
     * @param plugin The plugin to be registered.
     */
    fun addPlugin(plugin: Plugin) {
        plugin.register(this)
        this.plugins.add(plugin)
    }

    /**
     * Unregisters a plugin to this Ripe instance.
     *
     * @param plugin The plugin to be unregistered.
     */
    fun removePlugin(plugin: Plugin) {
        plugin.unregister()
        this.plugins.remove(plugin)
    }

    /**
     * @suppress
     */
    private fun _getState(): Map<String, Any> {
        return mapOf(
                "parts" to this.parts,
                "initials" to this.initials,
                "engraving" to this.engraving
        )
    }

    /**
     * @suppress
     */
    private fun _setOptions(options: Map<String, Any>) {
        this.options = options.toMutableMap()
        this.variant = options["variant"] as? String
        @Suppress("unchecked_cast")
        this.parts = options["parts"] as? MutableMap<String, Any> ?: HashMap()
        this.country = options["country"] as? String
        this.currency = options["country"] as? String
        this.locale = options["country"] as? String
        this.flag = options["country"] as? String
        this.useDefaults = options["useDefaults"] as? Boolean ?: true
        this.usePrice = options["usePrice"] as? Boolean ?: true
    }

    /**
     * @suppress
     */
    @JvmOverloads
    private fun _setPart(part: String, material: String?, color: String?, noEvents: Boolean = false) {
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
        val value: MutableMap<String, String?> = this.parts[part] as? MutableMap<String, String?>
                ?: HashMap()
        value["material"] = if (remove) null else material
        value["color"] = if (remove) null else color

        if (noEvents) {
            if (remove) {
                this.parts.remove(part)
            } else {
                this.parts[part] = value
            }
            return
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

    /**
     * @suppress
     */
    private fun _setParts(update: List<List<String?>>, noEvents: Boolean) {
        update.forEach { this._setPart(it[0]!!, it[1], it[2], noEvents) }
    }

    /**
     * @suppress
     */
    private fun _partsList(parts: HashMap<String, Any>): ArrayList<ArrayList<String?>> {
        val partsList = ArrayList<ArrayList<String?>>()
        for ((key, value) in parts) {
            @Suppress("unchecked_cast")
            val part = value as Map<String, String>
            partsList.add(arrayListOf(key, part["material"], part["color"]))
        }
        return partsList
    }

    /**
     * @suppress
     */
    private fun _pushHistory() {
        if (this.parts.isEmpty()) {
            return
        }

        val historyParts = if (this.historyPointer > -1) this.history[this.historyPointer] else null
        if (this.parts == historyParts) {
            return
        }

        val parts = this.getPartsCopy()
        this.history = this.history.subList(0, this.historyPointer + 1)
        this.history.add(parts)
        this.historyPointer = this.history.size - 1
    }

    /**
     * @surpress
     *
     * Makes a deep copy of the provided parts map to avoid unintended changes.
     *
     * @param parts THe parts map to be cloned.
     */
    fun cloneParts(parts: Map<String, Any>): Map<String, Any> {
        val clone = HashMap<String, Any>()
        for ((key, value) in parts) {
            @Suppress("unchecked_cast")
            val part = value as Map<String, Any>
            clone[key] = part.toMap()
        }
        return clone
    }
}
