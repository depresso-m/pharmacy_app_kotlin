package com.example.kotlin_project.Fragments

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_project.Activities.DrugPage
import com.example.kotlin_project.Other.Drug
import com.example.kotlin_project.Other.DrugAdapter
import com.example.kotlin_project.Other.DrugDatabaseHelper
import com.example.kotlin_project.R
import com.google.firebase.database.*


class RecentFragment : Fragment() {

    private lateinit var adapter: DrugAdapter
    private lateinit var list: ArrayList<Drug>
    private lateinit var drugDatabaseHelper: DrugDatabaseHelper
    private lateinit var clearRecent: ImageButton
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recent, container, false)

        val toolbarTitle: TextView = requireActivity().findViewById(R.id.toolbar_title)
        toolbarTitle.visibility = View.VISIBLE
        toolbarTitle.text = "Недавние"

        clearRecent = requireActivity().findViewById(R.id.clear_recent)
        clearRecent.visibility = View.VISIBLE
        clearRecent.setOnClickListener { setClearRecent() }

        recyclerView = view.findViewById(R.id.recent_drugs_list)
        recyclerView.setHasFixedSize(true)
        list = ArrayList()

        drugDatabaseHelper = DrugDatabaseHelper(requireContext()) // Используем requireContext()
        getRecentDrugsFromLocalDB()

        adapter = DrugAdapter(requireContext(), list) // Используем requireContext()
        recyclerView.adapter = adapter

        // Устанавливаем обработчик нажатий на элементы списка
        adapter.setOnItemClickListener(object : DrugAdapter.OnItemClickListener {
            override fun onItemClick(drug: Drug) {
                // Открываем новую страницу с информацией о лекарстве
                val intent = Intent(requireContext(), DrugPage::class.java)
                intent.putExtra("drug_key", drug.key)
                startActivity(intent)
            }
        })

        return view
    }

    private fun getRecentDrugsFromLocalDB() {
        val recentDrugKeys = drugDatabaseHelper.recentDrugs

        if (recentDrugKeys != null && recentDrugKeys.isNotEmpty()) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Drug")

            for (drugKey in recentDrugKeys) {
                if (!drugKey.isNullOrEmpty()) {
                    databaseReference.child(drugKey).addListenerForSingleValueEvent(object : ValueEventListener {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val drug = snapshot.getValue(Drug::class.java)
                            if (drug != null) {
                                drug.key = snapshot.key
                                list.add(drug)
                                adapter.notifyDataSetChanged()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // обработка ошибки
                        }
                    })
                }
            }
        }
    }

    private fun setClearRecent() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Подтверждение очистки")
        builder.setMessage("Вы уверены, что хотите очистить недавние лекарства?")
        builder.setPositiveButton("Да") { dialog, which ->
            drugDatabaseHelper.clearDatabase()
            adapter.clearAndUpdate()
            recyclerView.adapter = adapter
        }
        builder.setNegativeButton("Нет") { dialog, which ->
            // Ничего не делаем
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearRecent.visibility = View.GONE
    }
}

