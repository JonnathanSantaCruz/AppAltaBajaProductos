package com.example.scannerdecodigodebarras;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash_Screen extends AppCompatActivity {

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_splash_screen);

            Animation animacion1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
            Animation animacion2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);

            ImageView logo1 = findViewById(R.id.logo_1);
            ImageView logo2 = findViewById(R.id.logo_2);

            logo1.setAnimation(animacion1);
            logo2.setAnimation(animacion2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Splash_Screen.this, IniciarSesion.class);
                    startActivity(intent);
                    finish();
                }
            }, 2500);
    }
}