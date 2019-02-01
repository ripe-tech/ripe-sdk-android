package com.ripe.android.visual

import android.widget.ImageView
import android.graphics.Bitmap

import com.ripe.android.base.Ripe
import com.ripe.android.util.DownloadImageDelegate
import com.ripe.android.util.DownloadImageTask

class Image constructor(private val imageView: ImageView, override val owner: Ripe, override val options: Map<String, Any> = HashMap()) :
        Visual(owner, options),
        DownloadImageDelegate {

    private var showInitials = this.options["showInitials"] as Boolean? ?: true
    private var initialsBuilder: (String, String, ImageView) -> HashMap<String, Any> = this.options["initialsBuilder"] as ((String, String, ImageView) -> HashMap<String, Any>)? ?: ::_initialsBuilder
    private var initials: String? = null
    private var engraving: String? = null
    private var url: String? = null

    override fun update(state: Map<String, Any>) {
        val brand = this.owner.brand as String
        val model = this.owner.model as String

        this.initials = state["initials"] as String? ?: this.initials
        this.engraving = state["engraving"] as String? ?: this.engraving
        var initialsSpec = if (initials != null && engraving !== null)
            this.initialsBuilder(initials!!, engraving!!, this.imageView)
            else HashMap()

        val url = this.owner._getImageUrl(
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

        val task = DownloadImageTask(this)
        task.execute(url)
        this.url = url
    }

    override fun deinit() {}

    override fun downloadImageResult(result: Bitmap) {
        this.imageView.setImageBitmap(result)
    }

    private fun _initialsBuilder(initials: String, engraving: String, view: ImageView): HashMap<String, Any> {
        return hashMapOf(
                "initials" to initials,
                "profile" to arrayOf(engraving)
        )
    }
}
