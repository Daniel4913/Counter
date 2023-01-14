package com.example.counter.adapters

import android.R.attr.label
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.data.Description
import com.example.counter.databinding.DescriptionItemBinding


class DescriptionListAdapter(private val onItemClicked: (Description) -> Unit) :
    ListAdapter<Description, DescriptionListAdapter.DescriptionViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionViewHolder {
        return DescriptionViewHolder(
            DescriptionItemBinding.inflate(
                LayoutInflater.from(
                    parent.context))
        )
    }

    override fun onBindViewHolder(holder: DescriptionViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    class DescriptionViewHolder(private val binding: DescriptionItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnLongClickListener {
        fun bind(description: Description) {
            binding.apply {
                descriptionDate.text = description.descriptionDate
                descriptionNote.text = description.descriptionNote
            }
        }


        override fun onLongClick(v: View?): Boolean {
            if (v != null) {

                Toast.makeText(v.context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            return true
        }

    }


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Description>(){
            override fun areItemsTheSame(oldItem: Description, newItem: Description): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Description, newItem: Description): Boolean {
                return oldItem.descriptionNote == newItem.descriptionNote
            }
        }
    }
}