package com.example.kotlin_project.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_project.R
import com.example.kotlin_project.activities.Login
import com.example.kotlin_project.adapter.CollectionAdapter
import com.example.kotlin_project.databinding.FragmentCollectionBinding
import com.example.kotlin_project.model.Collection
import com.example.kotlin_project.viewmodel.CollectionViewModel
import com.google.firebase.auth.FirebaseAuth

class CollectionFragment : Fragment() {

    private lateinit var binding: FragmentCollectionBinding
    private lateinit var adapter: CollectionAdapter
    private lateinit var userName: String
    private lateinit var collections: ArrayList<Collection>
    private lateinit var createCollectionBtn: ImageButton
    private lateinit var exitBtn: ImageButton
    private lateinit var helpBtn: ImageButton
    private lateinit var toolbarTitle: TextView
    private val viewModel: CollectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userName = arguments?.getString("userName") ?: ""
        collections = arguments?.getSerializable("collections") as? ArrayList<Collection> ?: arrayListOf()

        setupViews()
        loadInitialData()
        observeViewModel()

        FirebaseAuth.getInstance().uid?.let { viewModel.loadCollections(it) }
        observeViewModel()
    }


    private fun setupViews() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CollectionAdapter()
        binding.recyclerView.adapter = adapter

        binding.username.text = userName

        toolbarTitle = requireActivity().findViewById(R.id.toolbar_title)
        toolbarTitle.visibility = View.VISIBLE
        toolbarTitle.text = "Профиль"

        createCollectionBtn = requireActivity().findViewById(R.id.create_collection_btn)
        createCollectionBtn.visibility = View.VISIBLE
        createCollectionBtn.setOnClickListener {
            showCreateCollectionDialog()
        }

        helpBtn = requireActivity().findViewById(R.id.help_btn)
        helpBtn.visibility = View.VISIBLE
        helpBtn.setOnClickListener {
            val helpFragment = HelpFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, helpFragment)
                .addToBackStack(null)
                .commit()
        }

        exitBtn = requireActivity().findViewById(R.id.exit_btn)
        exitBtn.visibility = View.VISIBLE
        exitBtn.setOnClickListener {
            leaveFromApp()
        }

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
    }

    private fun loadInitialData() {
        adapter.setCollections(collections)
    }

    private fun observeViewModel() {
        viewModel.collections.observe(viewLifecycleOwner) { collections ->
            collections?.let {
                adapter.setCollections(ArrayList(it))
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun showCreateCollectionDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_create_collection, null)
        dialogBuilder.setView(dialogView)

        val collectionNameEditText =
            dialogView.findViewById<EditText>(R.id.collection_name_edit_text)

        dialogBuilder.setTitle("Создать новую коллекцию")
        dialogBuilder.setPositiveButton("Создать") { dialog, _ ->
            val collectionName = collectionNameEditText.text.toString()
            if (collectionName.isEmpty()) {
                Toast.makeText(requireContext(), "Введите название коллекции", Toast.LENGTH_SHORT)
                    .show()
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
        currentUser?.let { user ->
            viewModel.createNewCollection(collectionName)
            // После создания новой коллекции загружаем обновленный список
            viewModel.loadCollections(user.uid)
        }
    }

    private fun leaveFromApp() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Подтверждение выхода")
        builder.setMessage("Вы уверены, что хотите выйти из аккаунта?")
        builder.setPositiveButton("Да") { dialog, _ ->
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireActivity(), Login::class.java)
            startActivity(intent)
            Toast.makeText(requireContext(), "Выход произведен успешно", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
            dialog.dismiss()
        }
        builder.setNegativeButton("Нет") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        createCollectionBtn.visibility = View.GONE
        exitBtn.visibility = View.GONE
        helpBtn.visibility = View.GONE
    }
}
