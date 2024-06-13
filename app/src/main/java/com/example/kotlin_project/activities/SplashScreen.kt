package com.example.kotlin_project.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.kotlin_project.R
import com.example.kotlin_project.model.Drug
import com.example.kotlin_project.model.Collection
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

        // Получаем текущего пользователя Firebase
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser == null) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Инициализируем DrugViewModel
        drugViewModel = ViewModelProvider(this).get(DrugViewModel::class.java)

        // Инициализируем CollectionViewModel
        collectionViewModel = ViewModelProvider(this).get(CollectionViewModel::class.java)



        // Наблюдаем за LiveData в DrugViewModel
        drugViewModel.drugs.observe(this, Observer { drugs ->
            if (drugs != null && drugs.isNotEmpty()) {
                // После загрузки лекарств запускаем загрузку коллекций
                firebaseUser?.let { user ->
                    collectionViewModel.loadCollections(user.uid)
                    collectionViewModel.loadUserName(user.uid)
                } ?: run {
                    // Если пользователь не авторизован, переходим на экран входа
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        })

        // Наблюдаем за LiveData в CollectionViewModel
        collectionViewModel.collections.observe(this, Observer { collections ->
            if (collections != null && collections.isNotEmpty()) {
                // Когда и лекарства, и коллекции загружены, переходим в MainActivity
                collectionViewModel.userName.observe(this, Observer { userName ->
                    if (userName != null) {
                        drugViewModel.drugs.value?.let { navigateToMainActivity(it, collections, userName) }
                    } else {
                        Log.d("SplashScreen", "User name is null")
                        // Handle case when user name is null
                    }
                })
            }
        })

        // Запускаем загрузку данных о лекарствах
        drugViewModel.loadDrugs()
    }

    private fun navigateToMainActivity(drugs: List<Drug>, collections: List<Collection>, userName: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("drugs", ArrayList(drugs))
        intent.putExtra("collections", ArrayList(collections))
        intent.putExtra("userName", userName)

        Log.d("SplashScreen", "Navigating to MainActivity with userName: $userName")

        startActivity(intent)
        finish()
    }
}
