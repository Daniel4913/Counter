package com.example.counter.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.R
import com.example.counter.data.modelentity.CounterStatus
import com.example.counter.data.relations.EventWithEventLogs
import com.example.counter.databinding.OccurenceHomeItemBinding
import com.example.counter.util.TimeCounter

class OccurrenceActivitiesListAdapter(private val onItemClicked: (EventWithEventLogs) -> Unit) :
    ListAdapter<EventWithEventLogs, OccurrenceActivitiesListAdapter.OccurrenceViewHolder>(
        DiffCallback
    ) {

    class OccurrenceViewHolder(private val binding: OccurenceHomeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val context: Context = binding.timeToNext.context

        fun bind(occ: EventWithEventLogs) {
            binding.apply {
                icOccurrence.text = occ.event.eventIcon
                occurrenceName.text = occ.event.eventName
                occurrenceName.isSelected = true
                occurrenceName.setSingleLine()
                applyUnderscoreColor(occ.event.category.underscoreColor)

                if (occ.singleEventEventLogs.isNotEmpty()) {
                    val countTime = TimeCounter( occ.event,occ.singleEventEventLogs.first())
                    val intervalSeconds = countTime.getIntervalSeconds!!

                    val timeFrom =countTime.getSecondsPassed()
                    val timeTo =countTime.getSecondsTo(intervalSeconds)

                    timeToNext.text = countTime.secondsToComponents(timeTo)
                    timeFromLast.text = countTime.secondsToComponents(timeFrom)
                    if (occ.event.status != null) {
                        applyTimeColor(occ.event.status!!)

                    }
                } else {
                    timeFromLast.text = context.getString(R.string.no_logs)
                    timeToNext.text = context.getString(R.string.no_logs)
                }
            }
        }




        private fun applyTimeColor(status: CounterStatus) {

            when (status.name) {
                "Enough" -> {
                    binding.timeToNext.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.green
                        )
                    )
                }
                "Late" -> {
                    binding.timeToNext.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.red
                        )
                    )
                }
                "CloseTo" -> {
                    binding.timeToNext.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.orange
                        )
                    )
                }
            }
        }

        private fun applyUnderscoreColor(color: Int) {
            val context = binding.categoryUnderscore.context

            binding.categoryUnderscore.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    color
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OccurrenceViewHolder {
        return OccurrenceViewHolder(
            OccurenceHomeItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: OccurrenceViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<EventWithEventLogs>() {
            override fun areItemsTheSame(
                oldItem: EventWithEventLogs,
                newItem: EventWithEventLogs
            ): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: EventWithEventLogs,
                newItem: EventWithEventLogs
            ): Boolean {
                return oldItem.event.eventId == newItem.event.eventId
                        && oldItem.event.eventName == newItem.event.eventName
                        && oldItem.event.category == newItem.event.category
                        && oldItem.event.intervalFrequency == newItem.event.intervalFrequency
                        && oldItem.event.createDate == newItem.event.createDate
                        && oldItem.singleEventEventLogs[0].eventLogId == newItem.singleEventEventLogs[0].eventLogId
                        && oldItem.singleEventEventLogs[0].eventOwnerId == newItem.singleEventEventLogs[0].eventOwnerId
                        && oldItem.singleEventEventLogs[0].fullDate == newItem.singleEventEventLogs[0].fullDate
                        && oldItem.singleEventEventLogs[0].intervalSeconds == newItem.singleEventEventLogs[0].intervalSeconds
                        && oldItem.singleEventEventLogs[0].secondsPassed == newItem.singleEventEventLogs[0].secondsPassed
                        && oldItem.singleEventEventLogs[0].secondsToNext == newItem.singleEventEventLogs[0].secondsToNext
            }
        }
    }
}