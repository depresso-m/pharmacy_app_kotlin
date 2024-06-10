package com.example.kotlin_project.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.kotlin_project.other.Drug
import com.example.kotlin_project.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DrugPage : AppCompatActivity() {

    private var isAdmin = false
    private lateinit var drugNameTextView: EditText
    private lateinit var drugDescriptionTextView: EditText
    private lateinit var drugManufacturerTextView: EditText
    private lateinit var drugExpirationDateTextView: EditText
    private lateinit var drugManufacturerCountryTextView: EditText
    private lateinit var drugDosageFormTextView: EditText
    private lateinit var drugStructureTextView: EditText
    private lateinit var drugSpecialConditionTextView: EditText
    private lateinit var drugInteractionTextView: EditText
    private lateinit var drugIndicationsTextView: EditText
    private lateinit var drugContraIndicationsTextView: EditText
    private lateinit var drugOverdoseTextView: EditText
    private lateinit var drugSideEffectTextView: EditText
    private lateinit var saveBtn: ImageButton
    private lateinit var editBtn: ImageButton
    private lateinit var deleteBtn: ImageButton
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drug_page)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Добавляем кнопку "Назад"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Создаем TextView и настраиваем его для отображения заголовка
        val titleTextView = TextView(this)
        titleTextView.text = "О лекарстве"
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        titleTextView.setTextColor(Color.BLACK)
        titleTextView.gravity = Gravity.CENTER

        // Добавляем TextView в Toolbar
        toolbar.addView(titleTextView)

        // Получаем ссылку на узел "User" в Firebase Realtime Database
        val databaseReference = FirebaseDatabase.getInstance().getReference("User")
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        // Получаем id текущего пользователя
        val userId = firebaseUser?.uid ?: return

        saveBtn = findViewById(R.id.conftim_changes_btn)
        editBtn = findViewById(R.id.edit_btn)
        deleteBtn = findViewById(R.id.delete_btn)

        // Проверяем, является ли текущий пользователь администратором
        databaseReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Получаем тип пользователя из базы данных
                val userType = snapshot.child("type").getValue(String::class.java)
                // Если пользователь является администратором, устанавливаем флаг isAdmin в true
                isAdmin = userType != null && userType == "Admin"

                // Обновляем видимость кнопок в зависимости от флага isAdmin
                saveBtn.visibility = if (isAdmin) View.VISIBLE else View.GONE
                editBtn.visibility = if (isAdmin) View.VISIBLE else View.GONE
                deleteBtn.visibility = if (isAdmin) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Обрабатываем ошибку чтения данных из базы данных
            }
        })

        val addToCollectionBtn: ImageButton = findViewById(R.id.add_to_collection_btn)
        addToCollectionBtn.setOnClickListener { showCollectionDialog() }

        drugNameTextView = findViewById(R.id.name_text_view)
        drugDescriptionTextView = findViewById(R.id.description_text_view)
        drugManufacturerTextView = findViewById(R.id.manufacturer_text_view)
        drugExpirationDateTextView = findViewById(R.id.expiration_date_text_view)
        drugManufacturerCountryTextView = findViewById(R.id.manufacturer_country_text_view)
        drugDosageFormTextView = findViewById(R.id.dosage_form_text_view)
        drugStructureTextView = findViewById(R.id.structure_text_view)
        drugSpecialConditionTextView = findViewById(R.id.special_conditions_text_view)
        drugInteractionTextView = findViewById(R.id.drug_interaction_text_view)
        drugIndicationsTextView = findViewById(R.id.indications_text_view)
        drugContraIndicationsTextView = findViewById(R.id.contraindications_text_view)
        drugOverdoseTextView = findViewById(R.id.overdose_text_view)
        drugSideEffectTextView = findViewById(R.id.side_effects_text_view)

        val drugKey = intent.getStringExtra("drug_key") ?: return
        mDatabase = FirebaseDatabase.getInstance().getReference("Drug").child(drugKey)

        // Чтение данных из базы данных
        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Получение данных из snapshot
                val drug = dataSnapshot.getValue(Drug::class.java)
                // Отображение данных в элементах TextView
                if (drug != null) {
                    drugNameTextView.setText(drug.name)
                    drugDescriptionTextView.setText(drug.description)
                    drugManufacturerTextView.setText(drug.manufacturer)
                    drugExpirationDateTextView.setText(drug.expiration_date)
                    drugManufacturerCountryTextView.setText(drug.manufacturer_country)
                    drugDosageFormTextView.setText(drug.dosage_form)
                    drugStructureTextView.setText(drug.structure)
                    drugSpecialConditionTextView.setText(drug.special_conditions)
                    drugInteractionTextView.setText(drug.drug_interaction)
                    drugIndicationsTextView.setText(drug.indications)
                    drugContraIndicationsTextView.setText(drug.contraindications)
                    drugOverdoseTextView.setText(drug.overdose)
                    drugSideEffectTextView.setText(drug.side_effects)
                } else {
                    Toast.makeText(this@DrugPage, "Данные не получены", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@DrugPage,
                    "Ошибка чтения данных: " + databaseError.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        saveBtn.setOnClickListener {
            // Проверяем, что все поля заполнены
            val updatedName = drugNameTextView.text.toString().trim()
            val updatedDescription = drugDescriptionTextView.text.toString().trim()
            val updatedManufacturer = drugManufacturerTextView.text.toString().trim()
            val updatedExpirationDate = drugExpirationDateTextView.text.toString().trim()
            val updatedManufacturerCountry = drugManufacturerCountryTextView.text.toString().trim()
            val updatedDosageForm = drugDosageFormTextView.text.toString().trim()
            val updatedStructure = drugStructureTextView.text.toString().trim()
            val updatedSpecialConditions = drugSpecialConditionTextView.text.toString().trim()
            val updatedDrugInteraction = drugInteractionTextView.text.toString().trim()
            val updatedIndications = drugIndicationsTextView.text.toString().trim()
            val updatedContraindications = drugContraIndicationsTextView.text.toString().trim()
            val updatedOverdose = drugOverdoseTextView.text.toString().trim()
            val updatedSideEffects = drugSideEffectTextView.text.toString().trim()

            if (updatedName.isEmpty() || updatedDescription.isEmpty() || updatedManufacturer.isEmpty() || updatedExpirationDate.isEmpty() ||
                updatedManufacturerCountry.isEmpty() || updatedDosageForm.isEmpty() || updatedStructure.isEmpty() ||
                updatedSpecialConditions.isEmpty() || updatedDrugInteraction.isEmpty() || updatedIndications.isEmpty() ||
                updatedContraindications.isEmpty() || updatedOverdose.isEmpty() || updatedSideEffects.isEmpty()
            ) {
                Toast.makeText(this, "Не должно быть пустых полей", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Создаем HashMap для хранения новых значений
            val updates = hashMapOf<String, Any>(
                "name" to updatedName,
                "description" to updatedDescription,
                "manufacturer" to updatedManufacturer,
                "expiration_date" to updatedExpirationDate,
                "manufacturer_country" to updatedManufacturerCountry,
                "dosage_form" to updatedDosageForm,
                "structure" to updatedStructure,
                "special_conditions" to updatedSpecialConditions,
                "drug_interaction" to updatedDrugInteraction,
                "indications" to updatedIndications,
                "contraindications" to updatedContraindications,
                "overdose" to updatedOverdose,
                "side_effects" to updatedSideEffects
            )

            // Обновляем данные в базе данных
            mDatabase.updateChildren(updates)
                .addOnCompleteListener(OnCompleteListener<Void> { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Изменения сохранены", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Ошибка сохранения изменений", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }

        editBtn.setOnClickListener {
            drugNameTextView.isEnabled = true
            drugNameTextView.isFocusable = true
            drugNameTextView.isFocusableInTouchMode = true

            drugDescriptionTextView.isEnabled = true
            drugDescriptionTextView.isFocusable = true
            drugDescriptionTextView.isFocusableInTouchMode = true

            drugManufacturerTextView.isEnabled = true
            drugManufacturerTextView.isFocusable = true
            drugManufacturerTextView.isFocusableInTouchMode = true

            drugExpirationDateTextView.isEnabled = true
            drugExpirationDateTextView.isFocusable = true
            drugExpirationDateTextView.isFocusableInTouchMode = true

            drugManufacturerCountryTextView.isEnabled = true
            drugManufacturerCountryTextView.isFocusable = true
            drugManufacturerCountryTextView.isFocusableInTouchMode = true

            drugDosageFormTextView.isEnabled = true
            drugDosageFormTextView.isFocusable = true
            drugDosageFormTextView.isFocusableInTouchMode = true

            drugStructureTextView.isEnabled = true
            drugStructureTextView.isFocusable = true
            drugStructureTextView.isFocusableInTouchMode = true

            drugSpecialConditionTextView.isEnabled = true
            drugSpecialConditionTextView.isFocusable = true
            drugSpecialConditionTextView.isFocusableInTouchMode = true

            drugInteractionTextView.isEnabled = true
            drugInteractionTextView.isFocusable = true
            drugInteractionTextView.isFocusableInTouchMode = true

            drugIndicationsTextView.isEnabled = true
            drugIndicationsTextView.isFocusable = true
            drugIndicationsTextView.isFocusableInTouchMode = true

            drugContraIndicationsTextView.isEnabled = true
            drugContraIndicationsTextView.isFocusable = true
            drugContraIndicationsTextView.isFocusableInTouchMode = true

            drugOverdoseTextView.isEnabled = true
            drugOverdoseTextView.isFocusable = true
            drugOverdoseTextView.isFocusableInTouchMode = true

            drugSideEffectTextView.isEnabled = true
            drugSideEffectTextView.isFocusable = true
            drugSideEffectTextView.isFocusableInTouchMode = true

            Toast.makeText(this@DrugPage, "Включен режим редактирования", Toast.LENGTH_SHORT).show()
        }


        deleteBtn.setOnClickListener {
            // Создаем диалоговое окно для подтверждения удаления
            AlertDialog.Builder(this)
                .setTitle("Подтвердите удаление")
                .setMessage("Вы уверены, что хотите удалить этот препарат?")
                .setPositiveButton("Да") { dialog: DialogInterface, _: Int ->
                    // Удаляем препарат из базы данных
                    mDatabase.removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Препарат удален", Toast.LENGTH_SHORT).show()
                            finish() // Закрываем активити после удаления
                        } else {
                            Toast.makeText(this, "Ошибка удаления препарата", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Нет") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    // Функция для отображения диалогового окна с коллекцией
    private fun showCollectionDialog() {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val addingPathDatabase = FirebaseDatabase.getInstance().getReference("Collection/$userUid")

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Добавить в аптечку")

        addingPathDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val collections = mutableListOf<String>()
                for (collectionSnapshot in snapshot.children) {
                    val collectionName = collectionSnapshot.key
                    if (collectionName != null) {
                        collections.add(collectionName)
                    }
                }

                val adapter = ArrayAdapter<String>(
                    applicationContext,
                    android.R.layout.simple_list_item_1,
                    collections
                )
                dialogBuilder.setAdapter(adapter, DialogInterface.OnClickListener { dialog, which ->
                    val selectedCollection = collections[which]
                    val drugKey = intent.getStringExtra("drug_key")

                    val contentRef = FirebaseDatabase.getInstance()
                        .getReference("Collection/$userUid/$selectedCollection/Content/$drugKey")
                    contentRef.setValue(true).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@DrugPage, "Добавлено в аптечку", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(this@DrugPage, "Ошибка добавления", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    dialog.dismiss()
                })

                val dialog = dialogBuilder.create()
                dialog.show()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    // Обрабатываем нажатие кнопки "Назад"
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
