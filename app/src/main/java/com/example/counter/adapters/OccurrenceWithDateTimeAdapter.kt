package com.example.counter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.data.relations.OccurrenceWithDatesTimes
import com.example.counter.databinding.OccurenceHomeItemBinding

class OccurrenceWithDateTimeAdapter(private val onItemClicked: (OccurrenceWithDatesTimes) -> Unit):
    ListAdapter<OccurrenceWithDatesTimes, OccurrenceWithDateTimeAdapter.OccdtViewHolder>(DiffCallback)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OccdtViewHolder {
        return OccdtViewHolder(
            OccurenceHomeItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: OccdtViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }


    class OccdtViewHolder(private val binding: OccurenceHomeItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(occ: OccurrenceWithDatesTimes){
            binding.apply {
                occurenceName.text = occ.occurence.occurenceName
                if(occ.occurrenceDatesTimes.isNotEmpty()){
                timeFromLast.text = occ.occurrenceDatesTimes.first().fullDate
                } else {
                    timeFromLast.text = "++"
                }
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<OccurrenceWithDatesTimes>() {
            override fun areItemsTheSame(oldItem: OccurrenceWithDatesTimes, newItem: OccurrenceWithDatesTimes): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: OccurrenceWithDatesTimes, newItem: OccurrenceWithDatesTimes): Boolean {
                return oldItem.occurence.occurenceName == newItem.occurence.occurenceName
            }
        }
    }
}