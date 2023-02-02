package com.example.counter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.R
import com.example.counter.data.relations.OccurrenceWithActivities
import com.example.counter.databinding.OccurenceHomeItemBinding
import com.example.counter.util.Constants
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

        lateinit var lastDateTime: String
        lateinit var intervalFrequency: String

        fun bind(occ: OccurrenceWithActivities) {
            binding.apply {
                icCategory.text = occ.occurrence.occurrenceName[1].toString()
                occurenceName.text = occ.occurrence.occurrenceName
                occurenceName.isSelected = true
                occurenceName.setSingleLine()

                if (occ.occurrenceActivities.isNotEmpty()) {

                    lastDateTime = occ.occurrenceActivities.last().fullDate
                    intervalFrequency = occ.occurrence.intervalFrequency

                    timeToNext.text = secondsToComponents(getSecondsTo(getIntervalSeconds()))

                    timeFromLast.text = secondsToComponents(getSecondsPassed())

                    applyTimeColor(secondsToComponents(getSecondsTo(getIntervalSeconds())))

                } else {
                    timeFromLast.text = "-"
                    timeToNext.text = "-"
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


        fun getSecondsTo(secondsTo: Long): Long {
            val timeFrom = lastDateTime
            val timeTo = secondsTo

            val pattern = "HH:mm:ss dd.MM.yyyy"
            val formatter = DateTimeFormatter.ofPattern(pattern)
            val lastDate = LocalDateTime.parse(timeFrom, formatter)
            val calculatedToDay = lastDate.plusSeconds(timeTo)
            val secondsTo = ChronoUnit.SECONDS.between(
                LocalDateTime.now(),
                calculatedToDay,
            )
            return secondsTo

        }
        fun getSecondsPassed(): Long {
            val today = LocalDateTime.now()
            val pattern = "HH:mm:ss dd.MM.yyyy"
            val formatter = DateTimeFormatter.ofPattern(pattern)
            val lastDate = LocalDateTime.parse(lastDateTime, formatter)
            val secondsPassed = ChronoUnit.SECONDS.between(
                lastDate,
                today
            )
            return secondsPassed
        }
       private fun secondsToComponents(secondsPassed: Long): String {
            secondsPassed.seconds.toComponents { days, hours, minutes, seconds, nanoseconds ->
                var calculated = ""

                when (days) {
                    0L -> calculated = "${hours}h ${minutes}m ${seconds}s"
                    else -> calculated = "${days}d ${hours}h ${minutes}m ${seconds}s"
                }

                return calculated
            }
        }
//

        private fun applyTimeColor(timeString: String) {
            val context = binding.timeToNext.context
            if (
                !timeString.contains("-") &&
                timeString.contains("d") &&
                timeString.contains("0h") ||
                timeString.contains("1h")

            ) {
                binding.timeToNext.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.orange
                    )
                )
            }
            else if (timeString.contains("-")) {
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
            }
        }
    }
}