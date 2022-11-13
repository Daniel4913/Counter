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

    class DatesTimesViewHolder(private var binding: DatesTimesItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(dateTime: DateTime){
            binding.apply {
                dateFull.text = dateTime.fullDate
                timeFrom.text = dateTime.timeStart
                timeLast.text = dateTime.occurenceOwnerId.toString()
                timeSpentWithTimer.text = dateTime.totalTime
            }
        }
    }

    override fun getItem(position: Int): DateTime {
        return super.getItem(position)
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