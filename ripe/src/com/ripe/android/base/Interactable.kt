package com.ripe.android.base

interface Interactable {
    val owner: Ripe
    val options: Map<String, Any>?

    fun update(state: Map<String, Any>)
    fun deinit()
}

class InteractableImpl constructor(override val owner: Ripe, override val options: Map<String, Any>?) : Interactable {
    override fun update(state: Map<String, Any>) {}
    override fun deinit() {}
}
