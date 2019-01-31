package com.ripe.android.base

open class Interactable constructor(open val owner: Ripe, open val options: Map<String, Any>?) : Observable() {
    open fun update(state: Map<String, Any>) {}
    open fun deinit() {}
}
