package com.ripe.android.util

import java.net.URL
import android.os.AsyncTask

interface DownloadURLDelegate {
    fun downloadURLResult(result: String)
}

class DownloadURLTask constructor(private val delegate: DownloadURLDelegate?) : AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg params: String?): String {
        val urlS = params[0] as String
        val url = URL(urlS)
        return url.readText()
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        this.delegate?.downloadURLResult(result)
    }
}
