package com.ripe.android.visual

import java.net.URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.ImageView
import android.graphics.BitmapFactory
import com.ripe.android.base.Ripe

/**
 * Reactively updates the image of an ImageView whenever the state of its owner changes.
 * An Image can be configured with the following options:
 *
 * - **showInitials** - A [Boolean] indicating if the owner's personalization should be shown. Defaults to true.
 * - **initialsBuilder** - A method that receives the *initials* and *engraving* as Strings and the ImageView that
 * will be used and returns a map with the initials and a profile list. This is the default method:
 *
 * ```
 *      fun initialsBuilder(initials: String, engraving: String, view: ImageView): Map<String, Any> {
 *          return hashMapOf(
 *              "initials" to initials,
 *              "profile" to arrayOf(engraving)
 *          )
 *      }
 * ```
 *
 *
 * @property imageView The ImageView that should be updated.
 * @property owner The [Ripe] instance to be shown.
 * @property options A map with options to configure the image.
 *
 * @constructor Constructs a new Image with the imageView, a Ripe instance as owner and a options map.
 *
 */
class Image constructor(private val imageView: ImageView, override val owner: Ripe, override val options: Map<String, Any> = HashMap()) :
        Visual(owner, options) {

    private var showInitials = this.options["showInitials"] as Boolean? ?: true
    @Suppress("unchecked_cast")
    private var initialsBuilder: (String, String, ImageView) -> Map<String, Any> = this.options["initialsBuilder"] as ((String, String, ImageView) -> Map<String, Any>)? ?: ::initialsBuilder
    private var initials: String? = null
    private var engraving: String? = null
    private var url: String? = null

    override fun update(state: Map<String, Any>) {
        val brand = this.owner.brand as String
        val model = this.owner.model as String

        this.initials = state["initials"] as String? ?: this.initials
        this.engraving = state["engraving"] as String? ?: this.engraving
        val initialsSpec = if (initials != null && engraving !== null && this.showInitials)
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
            val streamUrl = URL(url)
            val inputStream = streamUrl.openStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            withContext(Dispatchers.Main) {
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    override fun deinit() {}

    private fun initialsBuilder(initials: String, engraving: String, view: ImageView): Map<String, Any> {
        return hashMapOf(
                "initials" to initials,
                "profile" to arrayOf(engraving)
        )
    }
}
