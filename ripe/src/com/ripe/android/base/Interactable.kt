package com.ripe.android.base


/**
 * An entity that reactively represents a Ripe instance.
 *
 * @property owner The ripe instance that will be represented.
 * @property options A map with options to configure the instance.
 */
open class Interactable constructor(open val owner: Ripe, open val options: Map<String, Any>?) : Observable() {

    /**
     * This method is called by the owner whenever its state changes
     * so that the instance can update itself for the new state.
     *
     * @param state A map containing the new state of the owner.
     */
    open fun update(state: Map<String, Any>) {}

    /**
     * This method is called by the owner when it is deinitializing
     * so that any necessary cleanup operations can be executed.
     */
    open fun deinit() {}
}
