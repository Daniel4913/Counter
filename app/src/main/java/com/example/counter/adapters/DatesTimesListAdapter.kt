package com.example.counter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.data.DateTime
import com.example.counter.databinding.DatesTimesItemBinding

class DatesTimesListAdapter(private val onItemClicked: (DateTime) -> Unit):
    ListAdapter<DateTime, DatesTimesListAdapter.DatesTimesViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatesTimesViewHolder {
        return DatesTimesViewHolder(
            DatesTimesItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: DatesTimesViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    class DatesTimesViewHolder(private var binding: DatesTimesItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(dateTime: DateTime){
            binding.apply {
                dateFull.text = dateTime.fullDate
                totalTime.text = dateTime.timeStart
                occurenceOwner.text = dateTime.occurenceOwnerId.toString()
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DateTime>(){
            override fun areItemsTheSame(oldItem: DateTime, newItem: DateTime): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: DateTime, newItem: DateTime): Boolean {
                return oldItem.dateTimeId == newItem.dateTimeId
            }

        }
    }
}