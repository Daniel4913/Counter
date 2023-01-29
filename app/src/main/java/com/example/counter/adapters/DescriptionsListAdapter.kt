package com.example.counter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.data.Description
import com.example.counter.data.relations.OccurrenceWithDescripion
import com.example.counter.databinding.DescriptionItemBinding


class DescriptionsListAdapter(private val onItemClicked: (Description) -> Unit) :
    ListAdapter<Description, DescriptionsListAdapter.DescriptionsViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionsViewHolder {
        return DescriptionsViewHolder(
            DescriptionItemBinding.inflate(
                LayoutInflater.from(
                    parent.context))
        )
    }

    override fun onBindViewHolder(holder: DescriptionsViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    class DescriptionsViewHolder(private val binding: DescriptionItemBinding) :
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