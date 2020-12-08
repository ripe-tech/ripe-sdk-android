package com.ripe.android.visual

import com.ripe.android.base.Interactable
import com.ripe.android.base.Ripe

/**
 * This is a superclass for visual representations of a Ripe instance.
 *
 * @property owner The Ripe instance to be represented.
 * @property options A map with options to configure the instance.
 */
open class Visual constructor(override val owner: Ripe, override val options: Map<String, Any>?) : Interactable(owner, options)
