package com.example.kotlin_project.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.kotlin_project.R

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}