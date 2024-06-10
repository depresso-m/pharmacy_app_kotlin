package com.example.kotlin_project.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.kotlin_project.R

class HelpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_help, container, false)

        val toolbarTitle = activity?.findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle?.visibility = View.VISIBLE
        toolbarTitle?.text = "Справка"

        val toolbar = (activity as AppCompatActivity).findViewById<Toolbar>(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        val textView = view.findViewById<TextView>(R.id.textView)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Скрываем кнопку "Назад" после выхода из фрагмента
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
}