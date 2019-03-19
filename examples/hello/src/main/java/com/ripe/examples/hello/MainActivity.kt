package com.ripe.examples.hello

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
        val ripe = Ripe("dummy", "dummy")
        ripe.bindImage(imageView)
        ripe.bind("price") {
            val result = it as Map<String, Map<String, Any>>
            val total = result["total"]
            val priceFinal = total!!["price_final"] as Double
            val currency = total["currency"] as String
            textView.text = "${priceFinal} ${currency}"
        }
        ripe.bind("post_config") {
            ripe.setInitials("PT", "grey")
            ripe.setPart("piping", "leather_dmy", "brown")
        }
    }
}
