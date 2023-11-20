package com.example.a3dforge.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a3dforge.R

class TagsAdapter(private val tags: MutableList<String>, private val onClickRemove: (Int) -> Unit) :
    RecyclerView.Adapter<TagsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagTextView: TextView = itemView.findViewById(R.id.tagTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tag_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = tags[position]

        holder.tagTextView.text = tag
        holder.tagTextView.setOnClickListener {
            onClickRemove.invoke(position)
        }

        Log.d("TagsAdapter", "onBindViewHolder called for position $position")
        Log.d("TagsAdapter", holder.tagTextView.text as String)
    }

    override fun getItemCount(): Int {
        return tags.size
    }
}

