package com.example.kotlin_project.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.kotlin_project.R
import com.example.kotlin_project.viewmodel.CollectionViewModel
import com.example.kotlin_project.viewmodel.DrugViewModel
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {

    private lateinit var drugViewModel: DrugViewModel
    private lateinit var collectionViewModel: CollectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser == null) {
            navigateToLogin()
            return
        }

        drugViewModel = ViewModelProvider(this).get(DrugViewModel::class.java)
        collectionViewModel = ViewModelProvider(this).get(CollectionViewModel::class.java)

        drugViewModel.drugs.observe(this, Observer { drugs ->
            if (drugs != null && drugs.isNotEmpty()) {
                firebaseUser?.let { user ->
                    collectionViewModel.loadCollections(user.uid)
                    collectionViewModel.loadUserName(user.uid)
                } ?: run {
                    navigateToLogin()
                }
            }
        })

        collectionViewModel.collections.observe(this, Observer { collections ->
            if (collections != null) {
                collectionViewModel.userName.observe(this, Observer { userName ->
                    if (userName != null) {
                        navigateToMainActivity()
                    } else {
                        navigateToLogin()
                    }
                })
            }
        })

        drugViewModel.loadDrugs()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}
