package com.example.kotlin_project.other

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class DataViewModel : ViewModel() {
    private val _drugs = MutableLiveData<List<Drug>>()
    val drugs: LiveData<List<Drug>> get() = _drugs

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("Drug")

    fun loadDrugs() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Drug>()
                for (dataSnapshot in snapshot.children) {
                    val drug = dataSnapshot.getValue(Drug::class.java)
                    drug?.key = dataSnapshot.key
                    drug?.let { list.add(it) }
                }
                _drugs.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибок
            }
        })
    }
}
