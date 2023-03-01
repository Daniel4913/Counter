package com.example.counter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.R
import com.example.counter.data.modelentity.Occurrence
import com.example.counter.data.relations.OccurrenceWithActivities
import com.example.counter.databinding.OccurenceHomeItemBinding
import com.example.counter.util.Constants
import com.example.counter.util.Constants.Companion.DEFAULT_FORMATTER
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
                icOccurrence.text = occ.occurrence.occurrenceName[1].toString()
                occurenceName.text = occ.occurrence.occurrenceName
                occurenceName.isSelected = true
                occurenceName.setSingleLine()
                occurIcon.setImageResource(getOccurIcon(occ.occurrence))

                if (occ.occurrenceActivities.isNotEmpty()) {
                    lastDateTime = occ.occurrenceActivities.last().fullDate
                    intervalFrequency = occ.occurrence.intervalFrequency
                    timeToNext.text = secondsToComponents(getSecondsTo(getIntervalSeconds()))
                    timeFromLast.text = secondsToComponents(getSecondsPassed())

                    applyTimeColor(secondsToComponents(getSecondsTo(getIntervalSeconds())))
                } else {
                    timeFromLast.text = "- -"
                    timeToNext.text = "- -"
                }
            }
        }

        private fun getOccurIcon(occurrence: Occurrence): Int {
            val occurMore: Boolean = occurrence.occurMore
            return if (occurMore) {
                R.drawable.ic_expand_more
            } else {
                R.drawable.ic_expand_less
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

        private fun applyTimeColor(timeString: String) {
            val context = binding.timeToNext.context
            if (
                !timeString.contains("-") &&
                timeString.contains("0h") ||
                timeString.contains("1h") &&
                timeString.contains("11h") &&
                timeString.contains("21h") &&
                !timeString.contains("d")
            ) {
                binding.timeToNext.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.orange
                    )
                )
            } else if (timeString.contains("-")) {
                binding.timeToNext.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red_700
                    )
                )
            } else {
                binding.timeToNext.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
            }
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
                return oldItem.occurrence.occurrenceName == newItem.occurrence.occurrenceName
                        && oldItem.occurrence.occurrenceId == newItem.occurrence.occurrenceId
                        && oldItem.occurrenceActivities[0].secondsPassed == newItem.occurrenceActivities[0].secondsPassed
                        && oldItem.occurrenceActivities[0].secondsToNext == newItem.occurrenceActivities[0].secondsToNext
            }
        }
    }
}