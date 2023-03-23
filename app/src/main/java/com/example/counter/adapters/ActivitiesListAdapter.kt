package com.example.counter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.data.modelentity.EventLog
import com.example.counter.databinding.DatesTimesItemBinding
import kotlin.time.Duration.Companion.seconds

class ActivitiesListAdapter(private val onItemClicked: (EventLog) -> Unit, private val onLongItemClicked: (EventLog)-> Unit):
    ListAdapter<EventLog, ActivitiesListAdapter.DatesTimesViewHolder>(DiffCallback) {

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

        fun bind(eventLog: EventLog){
            binding.apply {
                date.text = eventLog.fullDate.split(" ")[1]
                hour.text = eventLog.fullDate.split(" ")[0]
                timeFrom.text = eventLog.secondsToNext?.let { secondsToComponents(it) }
                timeLast.text = eventLog.secondsPassed?.let { secondsToComponents(it) }


            }


        }

        private fun secondsToComponents(seconds: Long): String {
            seconds.seconds.toComponents { days, hours, minutes, seconds, _ ->

                return when (days) {
                    0L -> "${hours}h ${minutes}m "
                    else -> "${days}d ${hours}h ${minutes}m"
                }
            }
        }

    }



    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<EventLog>(){
            override fun areItemsTheSame(oldItem: EventLog, newItem: EventLog): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: EventLog, newItem: EventLog): Boolean {
                return oldItem.eventLogId == newItem.eventLogId
            }

        }
    }
}