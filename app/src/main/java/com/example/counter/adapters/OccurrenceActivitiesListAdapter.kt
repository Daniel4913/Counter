package com.example.counter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.R
import com.example.counter.data.relations.OccurrenceWithActivities
import com.example.counter.databinding.OccurenceHomeItemBinding
import com.example.counter.viewmodels.TimeCounter

class OccurrenceActivitiesListAdapter(private val onItemClicked: (OccurrenceWithActivities) -> Unit) :
    ListAdapter<OccurrenceWithActivities, OccurrenceActivitiesListAdapter.OccurrenceViewHolder>(
        DiffCallback
    ) {

    class OccurrenceViewHolder(private val binding: OccurenceHomeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(occ: OccurrenceWithActivities) {
            binding.apply {
                icCategory.text = occ.occurrence.occurrenceName[1].toString()
                occurenceName.text = occ.occurrence.occurrenceName
                occurenceName.isSelected = true
                occurenceName.setSingleLine()

                if (occ.occurrenceActivities.isNotEmpty()) {
                    val time = TimeCounter(occ.occurrence, occ.occurrenceActivities[0])
                    val secondsFrom = time.getSecondsPassed()
                    val secondsTo = time.getSecondsTo()
                    val calculatedSecondsTo = time.calculateSecondsTo(secondsTo)

                    timeToNext.text = time.secondsToComponents(calculatedSecondsTo)
                    timeFromLast.text = time.secondsToComponents(secondsFrom)
                    applyTimeColor(time.secondsToComponents(calculatedSecondsTo))

                } else {
                    timeFromLast.text = "-"
                    timeToNext.text = "-"
                }
            }
        }

        private fun applyTimeColor(timeString: String) {
            val context = binding.timeToNext.context
            if (
                !timeString.contains("-") &&
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
            if (timeString.contains("-")) {
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