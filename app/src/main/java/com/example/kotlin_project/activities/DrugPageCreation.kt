package com.example.kotlin_project.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.kotlin_project.other.Drug
import com.example.kotlin_project.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DrugPageCreation : AppCompatActivity() {
    private lateinit var drugNameEditText: EditText
    private lateinit var drugDescriptionEditText: EditText
    private lateinit var drugManufacturerEditText: EditText
    private lateinit var drugExpirationDateEditText: EditText
    private lateinit var drugManufacturerCountryEditText: EditText
    private lateinit var drugDosageFormEditText: EditText
    private lateinit var drugStructureEditText: EditText
    private lateinit var drugSpecialConditionEditText: EditText
    private lateinit var drugInteractionEditText: EditText
    private lateinit var drugIndicationsEditText: EditText
    private lateinit var drugContraIndicationsEditText: EditText
    private lateinit var drugOverdoseEditText: EditText
    private lateinit var drugSideEffectEditText: EditText

    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drug_page_creation)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val titleTextView = TextView(this).apply {
            text = "Добавление лекарств"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        }
        toolbar.addView(titleTextView)

        drugNameEditText = findViewById(R.id.name_edit_text)
        drugDescriptionEditText = findViewById(R.id.description_edit_text)
        drugManufacturerEditText = findViewById(R.id.manufacturer_edit_text)
        drugExpirationDateEditText = findViewById(R.id.expiration_date_edit_text)
        drugManufacturerCountryEditText = findViewById(R.id.manufacturer_country_edit_text)
        drugDosageFormEditText = findViewById(R.id.dosage_form_edit_text)
        drugStructureEditText = findViewById(R.id.structure_edit_text)
        drugSpecialConditionEditText = findViewById(R.id.special_conditions_edit_text)
        drugInteractionEditText = findViewById(R.id.drug_interaction_edit_text)
        drugIndicationsEditText = findViewById(R.id.indications_edit_text)
        drugContraIndicationsEditText = findViewById(R.id.contraindications_edit_text)
        drugOverdoseEditText = findViewById(R.id.overdose_edit_text)
        drugSideEffectEditText = findViewById(R.id.side_effects_edit_text)

        mDatabase = FirebaseDatabase.getInstance().reference
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_drug_creation_confirm, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return when (id) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_save -> {
                saveDrugToFirebase()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveDrugToFirebase() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Подтверждение добавления")
        builder.setMessage("Вы уверены, что хотите добавить этот препарат?")
        builder.setPositiveButton("Да") { dialog: DialogInterface, _: Int ->
            val name = drugNameEditText.text.toString().trim()
            val description = drugDescriptionEditText.text.toString().trim()
            val manufacturer = drugManufacturerEditText.text.toString().trim()
            val expirationDate = drugExpirationDateEditText.text.toString().trim()
            val manufacturerCountry = drugManufacturerCountryEditText.text.toString().trim()
            val dosageForm = drugDosageFormEditText.text.toString().trim()
            val structure = drugStructureEditText.text.toString().trim()
            val specialConditions = drugSpecialConditionEditText.text.toString().trim()
            val drugInteraction = drugInteractionEditText.text.toString().trim()
            val indications = drugIndicationsEditText.text.toString().trim()
            val contraindications = drugContraIndicationsEditText.text.toString().trim()
            val overdose = drugOverdoseEditText.text.toString().trim()
            val sideEffects = drugSideEffectEditText.text.toString().trim()

            if (name.isEmpty() || description.isEmpty() || manufacturer.isEmpty() || expirationDate.isEmpty() ||
                manufacturerCountry.isEmpty() || dosageForm.isEmpty() || structure.isEmpty() ||
                specialConditions.isEmpty() || drugInteraction.isEmpty() || indications.isEmpty() ||
                contraindications.isEmpty() || overdose.isEmpty() || sideEffects.isEmpty()) {
                Toast.makeText(this, "Не должно быть пустых полей", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val drug = Drug(name, description, manufacturer, expirationDate, manufacturerCountry,
                dosageForm, structure, specialConditions, drugInteraction, indications,
                contraindications, overdose, sideEffects, null)

            mDatabase.child("Drug").push().setValue(drug)

            Toast.makeText(this, "Препарат успешно добавлен", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Нет") { dialog: DialogInterface, _: Int ->
            // Ничего не делаем
        }
        builder.show()
    }
}
