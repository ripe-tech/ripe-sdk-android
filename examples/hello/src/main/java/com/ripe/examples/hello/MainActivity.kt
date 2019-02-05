package com.ripe.examples.hello

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

import com.ripe.android.base.Ripe
import com.ripe.examples.R
import java.util.*
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView = findViewById(R.id.imageView) as ImageView
        val ripe = Ripe("dummy", "dummy", hashMapOf(
                "url" to "https://sandbox.platforme.com/api/"
        ))
        ripe.bindImage(imageView)
        ripe.setInitials("PT", "grey")
        ripe.update()
        Timer().schedule(timerTask {
            ripe.api.getPrice { result, isValid ->
                print(result)
            }
        }, 500)

    }
}
