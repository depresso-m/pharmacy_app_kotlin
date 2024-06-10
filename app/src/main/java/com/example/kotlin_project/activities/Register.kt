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
import com.example.kotlin_project.other.User
import com.example.kotlin_project.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var editTextConfirmPassword: TextInputEditText
    private lateinit var editTextName: TextInputEditText
    private lateinit var buttonReg: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var textView: TextView
    private lateinit var mDataBase: DatabaseReference

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = mAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        mAuth = FirebaseAuth.getInstance()
        val USER_KEY = "User"
        mDataBase = FirebaseDatabase.getInstance().getReference(USER_KEY)

        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        editTextConfirmPassword = findViewById(R.id.confirm_password)
        editTextName = findViewById(R.id.name)
        buttonReg = findViewById(R.id.btn_register)
        progressBar = findViewById(R.id.progressBar)
        textView = findViewById(R.id.loginNow)

        textView.setOnClickListener {
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }

        buttonReg.setOnClickListener {
            val email: String
            val password: String
            val confirmPassword: String
            val type: String = mDataBase.key!!
            val name: String = editTextName.text.toString()
            email = editTextEmail.text.toString()
            password = editTextPassword.text.toString()

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Введите email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            confirmPassword = editTextConfirmPassword.text.toString()
            if (password != confirmPassword) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Успешная регистрация", Toast.LENGTH_SHORT).show()

                        // Получаем uid нового пользователя
                        val uid = task.result?.user?.uid ?: return@addOnCompleteListener

                        // Создаем новый объект User с указанным uid
                        val newUser = User("User", name, email, uid)

                        // Сохраняем новый объект User в Firebase Realtime Database
                        mDataBase.child(uid).setValue(newUser)

                        val intent = Intent(applicationContext, Login::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}