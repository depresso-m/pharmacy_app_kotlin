package com.example.kotlin_project.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.kotlin_project.R
import com.example.kotlin_project.fragments.CollectionFragment
import com.example.kotlin_project.fragments.RecentFragment
import com.example.kotlin_project.fragments.SearchFragment
import com.example.kotlin_project.model.Drug
import com.example.kotlin_project.model.Collection
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private var currentFragment: Fragment? = null
    private lateinit var toolbar: Toolbar
    private lateinit var bottomNavigationView: BottomNavigationView
    private var drugs: ArrayList<Drug>? = null
    private var collections: ArrayList<Collection>? = null
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser == null) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
            return
        }

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Получение данных из Intent, переданных из SplashScreen
        drugs = intent.getSerializableExtra("drugs") as? ArrayList<Drug>
        collections = intent.getSerializableExtra("collections") as? ArrayList<Collection>
        userName = intent.getStringExtra("userName") ?: ""

        // Если данные не удалось извлечь из Intent, инициализируем пустыми значениями
        drugs = drugs ?: ArrayList()
        collections = collections ?: ArrayList()

        // Начальная установка фрагмента SearchFragment с передачей drugs в аргументах
        currentFragment = SearchFragment().apply {
            arguments = Bundle().apply {
                putSerializable("drugs", drugs)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, currentFragment!!)
            .commit()

        // Настройка обработчика нажатий на элементы BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.action_item1 -> {
                    if (currentFragment is SearchFragment) return@setOnItemSelectedListener true
                    selectedFragment = SearchFragment().apply {
                        arguments = Bundle().apply {
                            putSerializable("drugs", drugs)
                        }
                    }
                }
                R.id.action_item2 -> {
                    if (currentFragment is RecentFragment) return@setOnItemSelectedListener true
                    selectedFragment = RecentFragment()
                }
                R.id.action_item3 -> {
                    if (currentFragment is CollectionFragment) return@setOnItemSelectedListener true
                    selectedFragment = CollectionFragment().apply {
                        arguments = Bundle().apply {
                            putSerializable("collections", collections)
                            putString("userName", userName)
                        }
                    }
                }
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit()
                currentFragment = selectedFragment
            }
            true
        }
    }
}
