package com.ripe.examples.hello

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

import com.ripe.android.base.Ripe
import com.ripe.examples.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView = findViewById(R.id.imageView) as ImageView
        val textView = findViewById(R.id.textView) as TextView
        val ripe = Ripe("dummy", "dummy", hashMapOf(
                "url" to "https://sandbox.platforme.com/api/"
        ))
        ripe.bind("price") {
            val result = it as Map<String, Map<String, Any>>
            val total = result["total"]!!
            val priceFinal = total["price_final"] as Double
            val currency = total["currency"] as String
            textView.text = "${priceFinal} ${currency}"
        }
        ripe.bindImage(imageView)
        ripe.setInitials("PT", "grey")
    }
}
