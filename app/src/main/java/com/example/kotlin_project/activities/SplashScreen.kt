package com.example.kotlin_project.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.kotlin_project.R
import com.example.kotlin_project.other.DataViewModel

class SplashScreen : AppCompatActivity() {

    private val dataViewModel: DataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        dataViewModel.drugs.observe(this, Observer { drugs ->
            if (drugs != null && drugs.isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("drugs", ArrayList(drugs)) // Передайте данные через Intent
                startActivity(intent)
                finish()
            }
        })

        // Загрузите данные
        dataViewModel.loadDrugs()
    }
}
