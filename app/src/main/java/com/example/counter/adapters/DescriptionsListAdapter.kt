package com.example.counter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.data.modelentity.Description
import com.example.counter.databinding.DescriptionItemBinding


class DescriptionsListAdapter :
    RecyclerView.Adapter<DescriptionsListAdapter.DescriptionsViewHolder>() {

    private var descriptions = emptyList<Description>()

    class DescriptionsViewHolder(private val binding: DescriptionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(description: Description) {
            binding.description = description
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): DescriptionsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DescriptionItemBinding.inflate(layoutInflater, parent, false)
                return DescriptionsViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionsViewHolder {
        return DescriptionsViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return descriptions.size
    }

    override fun onBindViewHolder(holder: DescriptionsViewHolder, position: Int) {
        val current = descriptions[position]
        holder.bind(current)
    }
    
    fun setData(descriptionsData: List<Description>){
        val descriptionsDiffUtil = DescriptionsDiffUtil(descriptions, descriptionsData)
        val descriptionsDiffResult = DiffUtil.calculateDiff(descriptionsDiffUtil)
        this.descriptions = descriptionsData
        descriptionsDiffResult.dispatchUpdatesTo(this)
    }
}