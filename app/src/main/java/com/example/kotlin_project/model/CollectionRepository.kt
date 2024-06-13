package com.example.kotlin_project.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CollectionRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("Collection")
    private val collectionsLiveData: MutableLiveData<List<Collection>> = MutableLiveData()

    init {
        loadCollections()
    }

    fun getCollections(): LiveData<List<Collection>> {
        return collectionsLiveData
    }

    fun createNewCollection(collectionName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val collectionRef = database.child(userId).child(collectionName)
            collectionRef.setValue(Collection(collectionName, ArrayList()))
        }
    }

    private fun loadCollections() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val collectionsRef = database.child(userId)
            collectionsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newCollections = mutableListOf<Collection>()
                    for (dataSnapshot in snapshot.children) {
                        val collection = dataSnapshot.getValue(Collection::class.java)
                        collection?.let { newCollections.add(it) }
                    }
                    collectionsLiveData.value = newCollections
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    fun getUserName(): MutableLiveData<String?> {
        val userNameLiveData = MutableLiveData<String?>()

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userRef = database.child("User").child(user.uid)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.child("name").getValue(String::class.java)
                    userNameLiveData.value = userName
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }

        return userNameLiveData
    }
}
