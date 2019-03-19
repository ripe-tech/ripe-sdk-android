package com.ripe.android.plugins

import com.ripe.android.base.Observable
import com.ripe.android.base.Ripe

/**
 * Base class of a Ripe Plugin.
 *
 * @property options A map with options to configure the plugin.
 */
open class Plugin constructor(open val options: Map<String, Any>?) : Observable() {
    /**
     * The Ripe instance that is using this plugin.
     */
    var owner: Ripe? = null

    /**
     * Registers this plugin to the provided Ripe instance.
     *
     * @param owner The Ripe instance to register to.
     */
    fun register(owner: Ripe) {
        this.owner = owner
    }

    /**
     * Unregisters this plugin from its owner.
     */
    fun unregister() {
        this.owner = null
    }
}
