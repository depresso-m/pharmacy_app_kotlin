package com.example.kotlin_project.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_project.activities.DrugPage
import com.example.kotlin_project.model.Drug
import com.example.kotlin_project.adapter.DrugAdapter
import com.example.kotlin_project.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList

class CollectionDetailsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var adapter: DrugAdapter
    private lateinit var emptyListText: TextView
    private val list = ArrayList<Drug>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collection_details, container, false)
        val collectionName = arguments?.getString("collection_name")
        activity?.title = collectionName

        recyclerView = view.findViewById(R.id.drug_list)
        recyclerView.setHasFixedSize(true)

        val toolbarTitle = activity?.findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle?.text = collectionName

        emptyListText = view.findViewById(R.id.empty_list_text)

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            val userUid = it.uid
            database = FirebaseDatabase.getInstance().getReference("Collection").child(userUid).child(collectionName!!).child("Content")
        }

        adapter = DrugAdapter(requireContext(), list)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : DrugAdapter.OnItemClickListener {
            override fun onItemClick(drug: Drug) {
                adapter.saveRecentDrug(drug.key)
                val intent = Intent(requireContext(), DrugPage::class.java)
                intent.putExtra("drug_key", drug.key)
                startActivity(intent)
            }
        })

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (dataSnapshot in snapshot.children) {
                    val drugKey = dataSnapshot.key
                    drugKey?.let { key ->
                        val drugRef = FirebaseDatabase.getInstance().getReference("Drug").child(key)
                        drugRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val drug = dataSnapshot.getValue(Drug::class.java)
                                drug?.let { drugObj ->
                                    drugObj.key = key
                                    list.add(drugObj)
                                    adapter.notifyDataSetChanged()
                                    checkEmptyList()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle error
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        checkEmptyList()
        return view
    }

    private fun checkEmptyList() {
        if (list.isEmpty()) {
            emptyListText.visibility = View.VISIBLE
        } else {
            emptyListText.visibility = View.GONE
        }
    }
}