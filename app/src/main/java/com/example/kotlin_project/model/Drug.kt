package com.example.kotlin_project.model


import java.io.Serializable

data class Drug(
    var name: String? = null,
    var description: String? = null,
    var manufacturer: String? = null,
    var expiration_date: String? = null,
    var manufacturer_country: String? = null,
    var dosage_form: String? = null,
    var structure: String? = null,
    var special_conditions: String? = null,
    var drug_interaction: String? = null,
    var indications: String? = null,
    var contraindications: String? = null,
    var overdose: String? = null,
    var side_effects: String? = null,
    var key: String? = null
) : Serializable
