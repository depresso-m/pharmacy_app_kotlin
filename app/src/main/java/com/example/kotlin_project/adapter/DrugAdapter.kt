package com.example.kotlin_project.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_project.R
import com.example.kotlin_project.model.Drug
import com.example.kotlin_project.util.DrugDatabaseHelper

class DrugAdapter(private val context: Context, private var drugs: ArrayList<Drug>) :
    RecyclerView.Adapter<DrugAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.drug_name)

        fun bind(drug: Drug) {
            nameTextView.text = drug.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drug, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val drug = drugs[position]
        holder.bind(drug)
        holder.itemView.setOnClickListener {
            saveRecentDrug(drug.key)
            listener?.onItemClick(drug)
        }
    }

    override fun getItemCount(): Int = drugs.size

    fun setDrugs(newDrugs: ArrayList<Drug>) {
        drugs = newDrugs
        notifyDataSetChanged()
    }

    fun clearAndUpdate() {
        drugs.clear()
        notifyDataSetChanged()
    }

    fun saveRecentDrug(drugId: String?) {
        val databaseHelper = DrugDatabaseHelper(context)
        if (drugId != null) {
            databaseHelper.saveRecentDrug(drugId)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(drug: Drug)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }
}
