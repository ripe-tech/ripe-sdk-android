package com.ripe.android.util

import java.net.URL
import android.graphics.Bitmap
import android.os.AsyncTask
import android.graphics.BitmapFactory

interface DownloadImageDelegate {
    fun downloadImageResult(result: Bitmap)
}

class DownloadImageTask constructor(private val delegate: DownloadImageDelegate?) : AsyncTask<String, Void, Bitmap>() {

    override fun doInBackground(vararg params: String?): Bitmap {
        val urlS = params[0] as String
        val url = URL(urlS)
        val inputStream = url.openStream()
        return BitmapFactory.decodeStream(inputStream)
    }

    override fun onPostExecute(result: Bitmap) {
        super.onPostExecute(result)
        this.delegate?.downloadImageResult(result)
    }
}
