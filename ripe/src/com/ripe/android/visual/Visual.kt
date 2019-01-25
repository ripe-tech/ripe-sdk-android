package com.ripe.android.visual

import com.ripe.android.base.*

interface Visual : Observable, Interactable

class VisualImpl constructor(override val owner: Ripe, override val options: Map<String, Any>?) :
        Observable by ObservableImpl(),
        Interactable by InteractableImpl(owner, options),
        Visual
