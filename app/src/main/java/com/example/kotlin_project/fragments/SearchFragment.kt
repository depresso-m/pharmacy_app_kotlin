package com.example.kotlin_project.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_project.activities.DrugPage
import com.example.kotlin_project.activities.DrugPageCreation
import com.example.kotlin_project.other.Drug
import com.example.kotlin_project.other.DrugAdapter
import com.example.kotlin_project.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {
    private var isAdmin = false
    private var searchView: SearchView? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var adapter: DrugAdapter
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var list: ArrayList<Drug>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = view.findViewById(R.id.drugs_list)
        database = FirebaseDatabase.getInstance().getReference("Drug")
        recyclerView.setHasFixedSize(true)

        // Ищем searchView и toolbar_title в activity_main.xml
        val activity = activity
        if (activity != null) {
            searchView = activity.findViewById(R.id.searchView)
            val toolbar_title = activity.findViewById<TextView>(R.id.toolbar_title)
            toolbar_title?.visibility = View.GONE
        }

        searchView?.visibility = View.VISIBLE

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Выполняется при нажатии кнопки "Поиск" на клавиатуре
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Выполняется при изменении текста в SearchView
                val filteredDrugs = filterDrugs(list, newText)
                adapter.setDrugs(filteredDrugs)
                return true
            }
        })

        // Получаем ссылку на узел "User" в Firebase Realtime Database
        val databaseReference = FirebaseDatabase.getInstance().getReference("User")
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        // Инициализируем floatingActionButton
        floatingActionButton = view.findViewById(R.id.floatingActionButton)

        // Проверяем, является ли текущий пользователь администратором
        firebaseUser?.let {
            val userId = it.uid
            databaseReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Получаем тип пользователя из базы данных
                    val userType = snapshot.child("type").getValue(String::class.java)

                    // Если пользователь является администратором, устанавливаем флаг isAdmin в true
                    isAdmin = userType == "Admin"

                    // Обновляем видимость кнопки floatingActionButton в зависимости от флага isAdmin
                    floatingActionButton.visibility = if (isAdmin) View.VISIBLE else View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    // Обрабатываем ошибку чтения данных из базы данных
                }
            })
        }

        // Устанавливаем слушатель нажатий на floatingActionButton
        floatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), DrugPageCreation::class.java)
            startActivity(intent)
        }

        list = ArrayList()
        adapter = DrugAdapter(requireContext(), list)
        recyclerView.adapter = adapter

        // Устанавливаем обработчик нажатий на элементы списка
        adapter.setOnItemClickListener(object : DrugAdapter.OnItemClickListener {
            override fun onItemClick(drug: Drug) {
                // Сохраняем информацию о недавно открытом лекарстве в базе данных
                adapter.saveRecentDrug(drug.key)

                // Открываем новую страницу с информацией о лекарстве
                val intent = Intent(requireContext(), DrugPage::class.java)
                intent.putExtra("drug_key", drug.key)
                startActivity(intent)
            }
        })


        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (dataSnapshot in snapshot.children) {
                    val drug = dataSnapshot.getValue(Drug::class.java)
                    drug?.key = dataSnapshot.key
                    drug?.let { list.add(it) }
                }
                adapter.setDrugs(list)
            }

            override fun onCancelled(error: DatabaseError) {
                // обработка ошибки
            }
        })

        return view
    }

    // Метод фильтрования списка на основе введенного текста
    private fun filterDrugs(drugs: ArrayList<Drug>, query: String): ArrayList<Drug> {
        val filteredDrugs = ArrayList<Drug>()
        for (drug in drugs) {
            if (drug.name?.toLowerCase()?.contains(query.toLowerCase()) == true) {
                filteredDrugs.add(drug)
            }
        }
        return filteredDrugs
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView?.visibility = View.GONE
    }
}