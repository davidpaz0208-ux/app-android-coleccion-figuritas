package com.example.stickers;

import android.os.Bundle;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class PackOpeningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_opening);

        ImageView pack = findViewById(R.id.packImage);

        ScaleAnimation anim = new ScaleAnimation(
                1f, 1.3f,
                1f, 1.3f,
                0.5f, 0.5f
        );

        anim.setDuration(800);
        anim.setRepeatCount(2);
        anim.setRepeatMode(ScaleAnimation.REVERSE);

        pack.startAnimation(anim);

        new android.os.Handler().postDelayed(this::finish, 2500);
    }
}
