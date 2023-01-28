package com.example.counter.adapters

import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.counter.data.relations.OccurrenceWithDatesTimes
import com.example.counter.databinding.OccurenceHomeItemBinding
import com.example.counter.viewmodels.CounterViewModel
import com.example.counter.viewmodels.TimeCounter

class OccurrenceWithDateTimeAdapter(private val onItemClicked: (OccurrenceWithDatesTimes) -> Unit):
    ListAdapter<OccurrenceWithDatesTimes, OccurrenceWithDateTimeAdapter.OccdtViewHolder>(DiffCallback)
{

    private lateinit var viewModel: CounterViewModel

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

                    timeFromLast.text = time.secondsToComponents(secondsFrom)

                } else {
                    timeFromLast.text = "-"
                }

                if(occ.occurrenceDatesTimes.isNotEmpty()){
                    val time = TimeCounter(occ.occurence,occ.occurrenceDatesTimes[0])
                    val secondsTo = time.getSecondsTo()
                    val calculatedSecondsTo = time.calculateSecondsTo(secondsTo)
                    timeToNext.text = time.secondsToComponents(calculatedSecondsTo)
                } else {
                    timeToNext.text = "-"
                }
            }
        }

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