package com.example.kotlin_project.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_project.activities.Login
import com.example.kotlin_project.other.Collection
import com.example.kotlin_project.other.CollectionAdapter
import com.example.kotlin_project.other.DrugDatabaseHelper
import com.example.kotlin_project.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class CollectionFragment : Fragment() {

    private lateinit var adapter: CollectionAdapter
    private val collections = ArrayList<Collection>()
    private lateinit var createCollectionBtn: ImageButton
    private lateinit var nameText: TextView
    private var userName: String? = null
    private lateinit var database: DatabaseReference
    private lateinit var exitBtn: ImageButton
    private lateinit var helpBtn: ImageButton
    private lateinit var databaseAddingPath: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collection, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        val toolbarTitle = activity?.findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle?.visibility = View.VISIBLE
        toolbarTitle?.text = "Профиль"

        adapter = CollectionAdapter(collections)
        recyclerView.adapter = adapter

        createCollectionBtn = activity?.findViewById(R.id.create_collection_btn)!!
        createCollectionBtn.visibility = View.VISIBLE
        createCollectionBtn.setOnClickListener {
            showCreateCollectionDialog()
        }

        databaseAddingPath = FirebaseDatabase.getInstance().reference

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            database = FirebaseDatabase.getInstance().getReference("Collection").child(userId)
        }

        nameText = view.findViewById(R.id.username)
        userName?.let { nameText.text = it }

        if (userName == null) {
            currentUser?.let {
                val userRef = FirebaseDatabase.getInstance().getReference("User").child(it.uid)
                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        userName = snapshot.child("name").getValue(String::class.java)
                        nameText.text = userName
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
            }
        }

        helpBtn = activity?.findViewById(R.id.help_btn)!!
        helpBtn.visibility = View.VISIBLE
        helpBtn.setOnClickListener {
            val helpFragment = HelpFragment()
            val bundle = Bundle()
            helpFragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, helpFragment)
                .addToBackStack(null)
                .commit()
        }

        exitBtn = activity?.findViewById(R.id.exit_btn)!!
        exitBtn.visibility = View.VISIBLE
        exitBtn.setOnClickListener {
            leaveFromApp()
        }

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newCollections = ArrayList<Collection>()
                for (dataSnapshot in snapshot.children) {
                    val collection = dataSnapshot.getValue(Collection::class.java)
                    collection?.let { newCollections.add(it) }
                }
                adapter.setCollections(newCollections)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        // Устанавливаем обработчик нажатий на элементы списка
        adapter.setOnItemClickListener(object : CollectionAdapter.OnItemClickListener {
            override fun onItemClick(collection: Collection?) {
                collection?.let { selectedCollection ->
                    val collectionDetailsFragment = CollectionDetailsFragment()
                    val bundle = Bundle()
                    bundle.putSerializable("collection", selectedCollection)
                    bundle.putString("collection_name", selectedCollection.name)
                    collectionDetailsFragment.arguments = bundle
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, collectionDetailsFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        })

        return view
    }

    private fun showCreateCollectionDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_create_collection, null)
        dialogBuilder.setView(dialogView)

        val collectionNameEditText = dialogView.findViewById<EditText>(R.id.collection_name_edit_text)

        dialogBuilder.setTitle("Создать новую коллекцию")
        dialogBuilder.setPositiveButton("Создать") { dialog, _ ->
            val collectionName = collectionNameEditText.text.toString()
            if (collectionName.isEmpty()) {
                Toast.makeText(requireContext(), "Введите название коллекции", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            createNewCollection(collectionName)
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        dialogBuilder.show()
    }

    private fun createNewCollection(collectionName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Пользователь не авторизован", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = currentUser.uid
        val collectionRef = databaseAddingPath.child("Collection").child(userId).child(collectionName)
        collectionRef.setValue(Collection(collectionName, ArrayList()))
        collections.add(Collection(collectionName, ArrayList()))
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        createCollectionBtn.visibility = View.GONE
        exitBtn.visibility = View.GONE
        helpBtn.visibility = View.GONE
    }

    private fun leaveFromApp() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Подтверждение выхода")
        builder.setMessage("Вы уверены, что хотите выйти из аккаунта?")
        builder.setPositiveButton("Да") { dialog, _ ->
            val drugDatabaseHelper = DrugDatabaseHelper(requireContext())
            drugDatabaseHelper.clearDatabase()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireActivity(), Login::class.java)
            startActivity(intent)
            Toast.makeText(requireActivity(), "Выход произведен успешно", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
            dialog.dismiss()
        }
        builder.setNegativeButton("Нет") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}