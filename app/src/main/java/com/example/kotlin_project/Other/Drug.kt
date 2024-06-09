package com.example.kotlin_project.Other

import com.google.firebase.database.DataSnapshot

class Drug {
    var name: String? = null //Название
    var description: String? = null //Описание
    var manufacturer: String? = null //Производитель
    var expiration_date: String? = null //Срок годности
    var manufacturer_country: String? = null //Страна происхождения
    var dosage_form: String? = null //Лекарственная форма
    var structure: String? = null //Состав
    var special_conditions: String? = null //Особые условия
    var drug_interaction: String? = null //Лекарственное взаимодействие
    var indications: String? = null //Показания
    var contraindications: String? = null //Противопоказания
    var overdose: String? = null //Передозировка
    var side_effects: String? = null //Побочные действия
    var key: String? = null //Ключ для firebase

    constructor(
        name: String?,
        description: String?,
        manufacturer: String?,
        expiration_date: String?,
        manufacturer_country: String?,
        dosage_form: String?,
        structure: String?,
        special_conditions: String?,
        drug_interaction: String?,
        indications: String?,
        contraindications: String?,
        overdose: String?,
        side_effects: String?,
        key: String?
    ) {
        this.name = name
        this.description = description
        this.manufacturer = manufacturer
        this.expiration_date = expiration_date
        this.manufacturer_country = manufacturer_country
        this.dosage_form = dosage_form
        this.structure = structure
        this.special_conditions = special_conditions
        this.drug_interaction = drug_interaction
        this.indications = indications
        this.contraindications = contraindications
        this.overdose = overdose
        this.side_effects = side_effects
        this.key = key
    }

    constructor()
    constructor(dataSnapshot: DataSnapshot) {
        // ... извлекайте значения из dataSnapshot и устанавливайте их для ваших полей класса
        key = dataSnapshot.key
    }
}