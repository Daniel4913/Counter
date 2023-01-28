package com.example.counter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.R
import com.example.counter.data.DateTime
import com.example.counter.data.relations.OccurrenceWithDatesTimes
import com.example.counter.databinding.OccurenceHomeItemBinding
import com.example.counter.viewmodels.CounterViewModel
import com.example.counter.viewmodels.TimeCounter

class OccurrenceWithDateTimeAdapter(private val onItemClicked: (OccurrenceWithDatesTimes) -> Unit):
    ListAdapter<OccurrenceWithDatesTimes, OccurrenceWithDateTimeAdapter.OccdtViewHolder>(DiffCallback)
{

    private lateinit var viewModel: CounterViewModel

    private var datesTimes = emptyList<DateTime>()

    class OccdtViewHolder(private val binding: OccurenceHomeItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(occ: OccurrenceWithDatesTimes){

            binding.apply {
                occurenceName.text = occ.occurence.occurenceName
                occurenceName.isSelected = true
                occurenceName.setSingleLine()

                if(occ.occurrenceDatesTimes.isNotEmpty()){
                    val time = TimeCounter(occ.occurence,occ.occurrenceDatesTimes[0])
                    val secondsFrom = time.getSecondsPassed()
                    val secondsTo = time.getSecondsTo()
                    val calculatedSecondsTo = time.calculateSecondsTo(secondsTo)

                    timeToNext.text = time.secondsToComponents(calculatedSecondsTo)
                    timeFromLast.text = time.secondsToComponents(secondsFrom)

                    applyRedTimeColor(time.secondsToComponents(calculatedSecondsTo))
                    applyOrangeTimeColor(time.secondsToComponents(calculatedSecondsTo))

                } else {
                    timeFromLast.text = "-"
                    timeToNext.text = "-"
                }
            }
        }

        private fun applyOrangeTimeColor(timeString: String) {
            val context = binding.timeToNext.context
                if(
                    !timeString.contains("-") &&
                    timeString.contains("1h") &&
                    timeString.contains("0h")
                ){
                    binding.timeToNext.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.orange
                        )
                    )
                }
        }

        private fun applyRedTimeColor(timeString: CharSequence){
            val context = binding.timeToNext.context
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