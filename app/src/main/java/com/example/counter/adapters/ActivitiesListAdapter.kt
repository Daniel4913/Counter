package com.example.counter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.data.modelentity.Activity
import com.example.counter.databinding.DatesTimesItemBinding

class ActivitiesListAdapter(private val onItemClicked: (Activity) -> Unit, private val onLongItemClicked: (Activity)-> Unit):
    ListAdapter<Activity, ActivitiesListAdapter.DatesTimesViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatesTimesViewHolder {
        return DatesTimesViewHolder(
            DatesTimesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: DatesTimesViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)

        holder.itemView.setOnLongClickListener {
            onLongItemClicked(current)
            true
        }
    }

    class DatesTimesViewHolder(private var binding: DatesTimesItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(activity: Activity){
            binding.apply {
                date.text = activity.fullDate
                timeFrom.text =  activity.secondsToNext.toString()
                timeLast.text = activity.secondsPassed.toString()

            }

        }

    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Activity>(){
            override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
                return oldItem.activityId == newItem.activityId
            }

        }
    }
}