package com.example.a3dforge.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a3dforge.R

class TagsModelAdapter(private val tags: MutableList<String>, private val onClickRemove: (Int) -> Unit) :
    RecyclerView.Adapter<TagsModelAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagNameTextView: TextView = itemView.findViewById(R.id.tagNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tag_model_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = tags[position]

        holder.tagNameTextView.text = tag
    }

    override fun getItemCount(): Int {
        return tags.size
    }
}

