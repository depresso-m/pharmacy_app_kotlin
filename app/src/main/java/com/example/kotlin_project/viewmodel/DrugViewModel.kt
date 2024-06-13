package com.example.kotlin_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.kotlin_project.model.Drug
import com.example.kotlin_project.model.DrugRepository

class DrugViewModel : ViewModel() {
    private val drugRepository = DrugRepository()
    val drugs: LiveData<List<Drug>> = drugRepository.getDrugs()

    fun loadDrugs(){
        drugRepository.getDrugs()
    }
}
