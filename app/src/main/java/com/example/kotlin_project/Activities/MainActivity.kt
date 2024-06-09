package com.example.kotlin_project.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.kotlin_project.Fragments.CollectionFragment
import com.example.kotlin_project.Fragments.RecentFragment
import com.example.kotlin_project.Fragments.SearchFragment
import com.example.kotlin_project.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var currentFragment: Fragment? = null
    private lateinit var toolbar: Toolbar
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        // Проверяем, авторизован ли пользователь
        if (firebaseUser == null) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        currentFragment = SearchFragment()
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            currentFragment!!
        ).commit()

        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            if (item.itemId == R.id.action_item1) {
                if (currentFragment is SearchFragment) {
                    // Уже открыт фрагмент поиска, ничего не делаем
                    return@setOnItemSelectedListener true
                }
                selectedFragment = SearchFragment()
            } else if (item.itemId == R.id.action_item2) {
                if (currentFragment is RecentFragment) {
                    // Уже открыт фрагмент с недавними, ничего не делаем
                    return@setOnItemSelectedListener true
                }
                selectedFragment = RecentFragment()
            } else if (item.itemId == R.id.action_item3) {
                if (currentFragment is CollectionFragment) {
                    // Уже открыт фрагмент коллекции, ничего не делаем
                    return@setOnItemSelectedListener true
                }
                selectedFragment = CollectionFragment()
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