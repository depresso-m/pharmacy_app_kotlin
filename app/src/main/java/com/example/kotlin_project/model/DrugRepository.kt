package com.example.kotlin_project.model

import com.google.firebase.database.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DrugRepository {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Drug")

    fun getDrugs(): LiveData<List<Drug>> {
        val drugsLiveData = MutableLiveData<List<Drug>>()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val drugs = mutableListOf<Drug>()
                for (dataSnapshot in snapshot.children) {
                    val drug = dataSnapshot.getValue(Drug::class.java)
                    drug?.key = dataSnapshot.key
                    drug?.let { drugs.add(it) }
                }
                drugsLiveData.value = drugs
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибок
            }
        })
        return drugsLiveData
    }
}
