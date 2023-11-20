package com.example.a3dforge.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a3dforge.R

class CategoriesAdapter(
    private val categories: MutableList<String>,
    private val onClickRemove: (Int) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryNameTextView)
        val removeCategoryImageView: ImageView = itemView.findViewById(R.id.removeCategoryImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.categoryNameTextView.text = categories[position]

        holder.removeCategoryImageView.setOnClickListener {
            onClickRemove.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}

