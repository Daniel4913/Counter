package com.example.counter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.data.Occurence
import com.example.counter.databinding.OccurenceHomeItemBinding

class OccurenceListAdapter(private val onItemClicked: (Occurence) -> Unit):
    ListAdapter<Occurence, OccurenceListAdapter.OccurenceViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OccurenceViewHolder {
        return OccurenceViewHolder(
            OccurenceHomeItemBinding.inflate(LayoutInflater.from(
                parent.context))
        )
    }

    override fun onBindViewHolder(holder: OccurenceViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)

    }

    class OccurenceViewHolder(private var binding: OccurenceHomeItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(occurence: Occurence){
            binding.apply {
                occurenceName.text = occurence.occurenceName
                timeFromLast.text = occurence.createDate
                timeToNext.text = occurence.createDate
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Occurence>(){
            override fun areItemsTheSame(oldItem: Occurence, newItem: Occurence): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Occurence, newItem: Occurence): Boolean {
                return oldItem.occurenceName == newItem.occurenceName
            }
        }
    }

}