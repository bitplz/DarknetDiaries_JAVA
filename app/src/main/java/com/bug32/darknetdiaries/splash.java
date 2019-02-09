package com.bug32.darknetdiaries;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.felipecsl.gifimageview.library.GifImageView;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class splash extends AppCompatActivity {

//    private GifImageView gifImageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

//        gifImageView = (GifImageView) findViewById(R.id.gifView);
        textView = (TextView) findViewById(R.id.mainTtile);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/VT323-Regular.otf");
        textView.setTypeface(typeface);

//        try {
//            InputStream inputStream = getAssets().open("gifSplash.gif");
//            byte[] bytes = IOUtils.toByteArray(inputStream);
//            gifImageView.setBytes(bytes);
//            gifImageView.startAnimation();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splash.this.startActivity(new Intent(splash.this, MainActivity.class));
                splash.this.finish();
            }
        },5000);
    }
}
