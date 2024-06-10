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
import com.example.kotlin_project.other.Drug
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var currentFragment: Fragment? = null
    private lateinit var toolbar: Toolbar
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var drugs: ArrayList<Drug>

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

        // Получаем данные из intent
        drugs = intent.getSerializableExtra("drugs") as ArrayList<Drug>

        // Передаем данные в SearchFragment
        currentFragment = SearchFragment().apply {
            arguments = Bundle().apply {
                putSerializable("drugs", drugs)
            }
        }
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container, currentFragment!!
        ).commit()

        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            if (item.itemId == R.id.action_item1) {
                if (currentFragment is SearchFragment) {
                    // Уже открыт фрагмент поиска, ничего не делаем
                    return@setOnItemSelectedListener true
                }
                selectedFragment = SearchFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("drugs", drugs)
                    }
                }
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
                    .replace(R.id.fragment_container, selectedFragment).commit()
                currentFragment = selectedFragment
            }
            true
        }
    }
}
