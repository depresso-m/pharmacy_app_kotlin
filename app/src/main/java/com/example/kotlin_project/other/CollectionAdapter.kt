package com.example.kotlin_project.other

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_project.R

class CollectionAdapter(private var collections: ArrayList<Collection>) :
    RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.collection_name)

        fun bind(collection: Collection) {
            nameTextView.text = collection.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_collection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val collection = collections[position]
        holder.bind(collection)
        holder.itemView.setOnClickListener {
            listener?.onItemClick(collection)
        }
    }

    override fun getItemCount(): Int = collections.size

    fun setCollections(newCollections: ArrayList<Collection>) {
        collections = newCollections
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(collection: Collection?)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }
}