package com.example.counter.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.R
import com.example.counter.data.modelentity.CounterStatus
import com.example.counter.data.modelentity.Occurrence
import com.example.counter.data.relations.OccurrenceWithActivities
import com.example.counter.databinding.OccurenceHomeItemBinding
import com.example.counter.util.Constants
import com.example.counter.util.Constants.Companion.DEFAULT_FORMATTER
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.seconds

class OccurrenceActivitiesListAdapter(private val onItemClicked: (OccurrenceWithActivities) -> Unit) :
    ListAdapter<OccurrenceWithActivities, OccurrenceActivitiesListAdapter.OccurrenceViewHolder>(
        DiffCallback
    ) {

    class OccurrenceViewHolder(private val binding: OccurenceHomeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var lastDateTime: String
        private lateinit var intervalFrequency: String

        fun bind(occ: OccurrenceWithActivities) {
            binding.apply {
                icOccurrence.text = occ.occurrence.occurrenceIcon
                occurrenceName.text = occ.occurrence.occurrenceName
                occurrenceName.isSelected = true
                occurrenceName.setSingleLine()

                applyUnderscoreColor(occ.occurrence.category.underscoreColor)

                if (occ.occurrenceActivities.isNotEmpty()) {
                    lastDateTime = occ.occurrenceActivities.last().fullDate
                    intervalFrequency = occ.occurrence.intervalFrequency
                    timeToNext.text = secondsToComponents(getSecondsTo(getIntervalSeconds()))
                    timeFromLast.text = secondsToComponents(getSecondsPassed())
                    if (occ.occurrence.status != null) {
                        applyTimeColor(occ.occurrence.status!!)

                    }
                } else {
                    timeFromLast.text = "- -"
                    timeToNext.text = "- -"
                }
            }
        }


        fun getIntervalSeconds(): Long {
            val interval = intervalFrequency
            val intervalValue = interval.split(" ")[0].toLong()
            val intervalFrequency = interval.split(" ")[1]
            var toSecondsTo: Long = 0
            when (intervalFrequency) {
                Constants.MINUTES -> {
                    toSecondsTo = 60 * intervalValue
                }
                Constants.HOURS -> {
                    toSecondsTo = 3600 * intervalValue
                }
                Constants.DAYS -> {
                    toSecondsTo = 86400 * intervalValue
                }
                Constants.WEEKS -> {
                    toSecondsTo = 604800 * intervalValue
                }
                Constants.MONTHS -> {
                    toSecondsTo = 2629800 * intervalValue
                }
            }
            return toSecondsTo
        }


        private fun getSecondsTo(secondsTo: Long): Long {
            val timeFrom = lastDateTime
            val formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMATTER)
            val lastDate = LocalDateTime.parse(timeFrom, formatter)
            val calculatedToDay = lastDate.plusSeconds(secondsTo)

            return ChronoUnit.SECONDS.between(
                LocalDateTime.now(),
                calculatedToDay,
            )
        }

        private fun getSecondsPassed(): Long {
            val today = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMATTER)
            val lastDate = LocalDateTime.parse(lastDateTime, formatter)

            return ChronoUnit.SECONDS.between(
                lastDate,
                today
            )
        }

        private fun secondsToComponents(seconds: Long): String {
            seconds.seconds.toComponents { days, hours, minutes, seconds, _ ->

                return when (days) {
                    0L -> "${hours}h ${minutes}m "
                    else -> "${days}d ${hours}h ${minutes}m"
                }
            }
        }

        private fun applyTimeColor(status: CounterStatus) {
            val context = binding.timeToNext.context
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
        private val DiffCallback = object : DiffUtil.ItemCallback<OccurrenceWithActivities>() {
            override fun areItemsTheSame(
                oldItem: OccurrenceWithActivities,
                newItem: OccurrenceWithActivities
            ): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: OccurrenceWithActivities,
                newItem: OccurrenceWithActivities
            ): Boolean {
                return oldItem.occurrence.occurrenceId == newItem.occurrence.occurrenceId
                        && oldItem.occurrence.occurrenceName == newItem.occurrence.occurrenceName
                        && oldItem.occurrence.category == newItem.occurrence.category
                        && oldItem.occurrence.intervalFrequency == newItem.occurrence.intervalFrequency
                        && oldItem.occurrence.createDate == newItem.occurrence.createDate
                        && oldItem.occurrenceActivities[0].activityId == newItem.occurrenceActivities[0].activityId
                        && oldItem.occurrenceActivities[0].occurrenceOwnerId == newItem.occurrenceActivities[0].occurrenceOwnerId
                        && oldItem.occurrenceActivities[0].fullDate == newItem.occurrenceActivities[0].fullDate
                        && oldItem.occurrenceActivities[0].intervalSeconds == newItem.occurrenceActivities[0].intervalSeconds
                        && oldItem.occurrenceActivities[0].secondsPassed == newItem.occurrenceActivities[0].secondsPassed
                        && oldItem.occurrenceActivities[0].secondsToNext == newItem.occurrenceActivities[0].secondsToNext
            }
        }
    }
}