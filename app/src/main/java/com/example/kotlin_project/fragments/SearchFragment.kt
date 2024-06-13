package com.example.kotlin_project.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_project.R
import com.example.kotlin_project.activities.DrugPage
import com.example.kotlin_project.activities.DrugPageCreation
import com.example.kotlin_project.databinding.FragmentSearchBinding
import com.example.kotlin_project.model.Drug
import com.example.kotlin_project.adapter.DrugAdapter
import com.example.kotlin_project.viewmodel.DrugViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {

    private val viewModel: DrugViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: DrugAdapter
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var toolbarTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false).apply {
            viewModel = this@SearchFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        toolbarTitle = requireActivity().findViewById(R.id.toolbar_title)
        toolbarTitle.visibility = View.GONE


        // Настройка RecyclerView
        adapter = DrugAdapter(requireContext(), arrayListOf())
        binding.drugsList.layoutManager = LinearLayoutManager(context)
        binding.drugsList.adapter = adapter

        // Настройка SearchView
        val searchView = requireActivity().findViewById<SearchView>(R.id.searchView)
        searchView.visibility = View.VISIBLE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val filteredDrugs = filterDrugs(viewModel.drugs.value ?: arrayListOf(), newText)
                adapter.setDrugs(filteredDrugs)
                return true
            }
        })

        // Наблюдаем за изменениями в списке лекарств
        viewModel.drugs.observe(viewLifecycleOwner, Observer { drugs ->
            adapter.setDrugs(ArrayList(drugs))
        })

        // Настройка кнопки FloatingActionButton
        floatingActionButton = binding.floatingActionButton
        setupFloatingActionButton()
        floatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), DrugPageCreation::class.java)
            startActivity(intent)
        }

        // Обработка кликов на элементы списка
        adapter.setOnItemClickListener(object : DrugAdapter.OnItemClickListener {
            override fun onItemClick(drug: Drug) {
                // Сохраняем информацию о недавно открытом лекарстве
                adapter.saveRecentDrug(drug.key)

                // Открываем страницу с информацией о лекарстве
                val intent = Intent(requireContext(), DrugPage::class.java)
                intent.putExtra("drug_key", drug.key)
                startActivity(intent)
            }
        })

        return binding.root
    }

    // Метод фильтрации списка на основе введенного текста
    private fun filterDrugs(drugs: List<Drug>, query: String): ArrayList<Drug> {
        val filteredDrugs = ArrayList<Drug>()
        for (drug in drugs) {
            if (drug.name?.contains(query, ignoreCase = true) == true) {
                filteredDrugs.add(drug)
            }
        }
        return filteredDrugs
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<SearchView>(R.id.searchView).visibility = View.GONE
    }

    private fun setupFloatingActionButton() {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("User")
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        firebaseUser?.let {
            val userId = it.uid
            databaseReference.child(userId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userType = snapshot.child("type").getValue(String::class.java)
                    val isAdmin = userType == "Admin"
                    floatingActionButton.visibility = if (isAdmin) View.VISIBLE else View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }

        floatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), DrugPageCreation::class.java)
            startActivity(intent)
        }
    }
}

