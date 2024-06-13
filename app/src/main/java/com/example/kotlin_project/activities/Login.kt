package com.example.kotlin_project.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.kotlin_project.R
import com.example.kotlin_project.viewmodel.CollectionViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.example.kotlin_project.model.Collection

class Login : AppCompatActivity() {
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonLogin: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var textView: TextView
    private lateinit var collectionViewModel: CollectionViewModel

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue))

        mAuth = FirebaseAuth.getInstance()
        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        buttonLogin = findViewById(R.id.btn_login)
        progressBar = findViewById(R.id.progressBar)
        textView = findViewById(R.id.registerNow)

        collectionViewModel = ViewModelProvider(this).get(CollectionViewModel::class.java)

        textView.setOnClickListener {
            val intent = Intent(applicationContext, Register::class.java)
            startActivity(intent)
            finish()
        }

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Введите email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Успешный вход", Toast.LENGTH_SHORT).show()

                        FirebaseAuth.getInstance().uid?.let { it1 -> loadUserDataAndNavigate(it1) }


                    } else {
                        Toast.makeText(this, "Ошибка входа", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun loadUserDataAndNavigate(userId: String) {
        collectionViewModel.loadCollections(userId)
        collectionViewModel.loadUserName(userId)

        // Наблюдаем за LiveData в CollectionViewModel
        collectionViewModel.collections.observe(this, Observer { collections ->
            if (collections != null && collections.isNotEmpty()) {
                collectionViewModel.userName.observe(this, Observer { userName ->
                    if (userName != null) {
                        navigateToMainActivity(collections, userName)
                    }
                    else
                    {
                    }
                })
            }
        })
    }
    private fun navigateToMainActivity(collections: List<Collection>, userName: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("collections", ArrayList(collections))
        intent.putExtra("userName", userName)
        startActivity(intent)
        finish()
    }
}

