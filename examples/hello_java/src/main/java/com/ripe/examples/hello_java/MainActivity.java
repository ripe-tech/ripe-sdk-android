package com.ripe.examples.hello_java;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.ripe.android.base.Ripe;

import java.util.Map;

import kotlin.Unit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.imageView);
        TextView textView = findViewById(R.id.textView);
        Ripe ripe = new Ripe("dummy", "dummy");
        ripe.bindImage(imageView);
        ripe.bind("price", result -> {
            Map total = (Map) result.get("total");
            Double priceFinal = (Double) total.get("price_final");
            String currency = (String) total.get("currency");
            textView.setText(priceFinal + " " + currency);
            return Unit.INSTANCE;
        });
        ripe.bind("post_config", result -> {
            ripe.setInitials("PT", "grey");
            ripe.setPart("piping", "leather_dmy", "brown");
            return Unit.INSTANCE;
        });
    }
}
