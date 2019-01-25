package com.ripe.android.visual

import android.widget.ImageView
import android.graphics.Bitmap

import com.ripe.android.base.Ripe
import com.ripe.android.util.DownloadImageDelegate
import com.ripe.android.util.DownloadImageTask

class Image constructor(private val imageView: ImageView, override val owner: Ripe, override val options: Map<String, Any> = HashMap()) :
        Visual by VisualImpl(owner, options),
        DownloadImageDelegate {

    override fun update(state: Map<String, Any>) {
        val brand = this.owner.brand as String
        val model = this.owner.model as String
        val url = this.owner._getImageUrl(hashMapOf("brand" to brand, "model" to model))
        val task = DownloadImageTask(this)
        task.execute(url)
    }

    override fun deinit() {}

    override fun downloadImageResult(result: Bitmap) {
        this.imageView.setImageBitmap(result)
    }
}
