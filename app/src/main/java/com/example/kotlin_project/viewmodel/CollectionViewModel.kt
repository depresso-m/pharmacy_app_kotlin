package com.example.kotlin_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlin_project.model.Collection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CollectionViewModel : ViewModel() {

    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?>
        get() = _userName

    private val _collections = MutableLiveData<List<Collection>>()
    val collections: LiveData<List<Collection>>
        get() = _collections

    private val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference
    }

    fun loadUserName(userId: String) {
        val userRef = database.child("User").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("name").getValue(String::class.java)
                _userName.value = userName
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun loadCollections(userId: String) {
        val collectionRef = database.child("Collection").child(userId)
        collectionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newCollections = mutableListOf<Collection>()
                for (dataSnapshot in snapshot.children) {
                    val collection = dataSnapshot.getValue(Collection::class.java)
                    collection?.let { newCollections.add(it) }
                }
                _collections.value = newCollections
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun createNewCollection(collectionName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val collectionRef = database.child("Collection").child(userId).child(collectionName)
            collectionRef.setValue(Collection(collectionName, ArrayList())).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // После успешного добавления, обновляем список коллекций

                } else {
                    // Обработка ошибки
                }
            }
        }
    }

}
