package com.ripe.android.visual

import java.net.URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.ImageView
import android.graphics.BitmapFactory
import com.ripe.android.base.Ripe

class Image constructor(private val imageView: ImageView, override val owner: Ripe, override val options: Map<String, Any> = HashMap()) :
        Visual(owner, options) {

    private var showInitials = this.options["showInitials"] as Boolean? ?: true
    @Suppress("unchecked_cast")
    private var initialsBuilder: (String, String, ImageView) -> HashMap<String, Any> = this.options["initialsBuilder"] as ((String, String, ImageView) -> HashMap<String, Any>)? ?: ::_initialsBuilder
    private var initials: String? = null
    private var engraving: String? = null
    private var url: String? = null

    override fun update(state: Map<String, Any>) {
        val brand = this.owner.brand as String
        val model = this.owner.model as String

        this.initials = state["initials"] as String? ?: this.initials
        this.engraving = state["engraving"] as String? ?: this.engraving
        var initialsSpec = if (initials != null && engraving !== null && this.showInitials)
            this.initialsBuilder(initials!!, engraving!!, this.imageView)
            else HashMap()

        val url = this.owner.api.getImageUrl(
            hashMapOf(
                "brand" to brand,
                "model" to model,
                "initials" to (initialsSpec["initials"] ?: ""),
                "profile" to (initialsSpec["profile"] ?: ArrayList<String>())
            )
        )
        if (this.url == url) {
            return
        }

        this.url = url
        val imageView = this.imageView
        CoroutineScope(Dispatchers.IO).launch {
            val url = URL(url)
            val inputStream = url.openStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            withContext(Dispatchers.Main) {
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    override fun deinit() {}

    private fun _initialsBuilder(initials: String, engraving: String, view: ImageView): HashMap<String, Any> {
        return hashMapOf(
                "initials" to initials,
                "profile" to arrayOf(engraving)
        )
    }
}
