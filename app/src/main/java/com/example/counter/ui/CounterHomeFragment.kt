package com.example.counter.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.R
import com.example.counter.adapters.OccurrenceActivitiesListAdapter
import com.example.counter.data.modelentity.Activity
import com.example.counter.data.relations.OccurrenceWithActivities
import com.example.counter.databinding.FragmentCounterHomeBinding
import com.example.counter.viewmodels.CounterViewModel


import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CounterHomeFragment : Fragment() {

    private lateinit var viewModel: CounterViewModel

    private var selectedCategoryChip = "All"
    private var selectedCategoryChipId = 0

    private var _binding: FragmentCounterHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCounterHomeBinding.inflate(inflater, container, false)

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = OccurrenceActivitiesListAdapter {
            val action =
                CounterHomeFragmentDirections.actionCounterHomeFragmentToOccurenceFragment(
                    it.occurrence.occurrenceId,
                    it.occurrence.occurrenceName
                )
            this.findNavController().navigate(action)
        }

        binding.occurenciesRecyclerView.adapter = adapter
        viewModel.readOccurrencesWithActivities.observe(this.viewLifecycleOwner) { items ->
            items.let { it ->
                try {
                    val items = it
                    val listSeconds = makeListOfSecondsTo(it)
                    val itemsBySeconds =
                        items.associateBy { it.occurrenceActivities.last().secondsToNext }
                    val sortedItems = listSeconds.sorted().map { itemsBySeconds[it] }
                    adapter.submitList(sortedItems)

                    /////////////////////
                    val bigOccurrence = sortedItems.first()
                    binding.occurencyNameLabel.text = bigOccurrence?.occurrence?.occurrenceName

                    binding.occurencyTimeToLabel.text = getString(
                        R.string.time_late,
                        bigOccurrence?.occurrenceActivities?.last()?.secondsToNext.toString()
                    )
                    binding.occurencyTimeFromLabel.text = getString(R.string.time_passed,bigOccurrence?.occurrenceActivities?.last()?.secondsPassed.toString() )

                } catch (e: java.lang.Exception) {
                    Toast.makeText(
                        context,
                        "List will be sorted when all Occurrences have at least one activity",
                        Toast.LENGTH_LONG
                    ).show()
                    adapter.submitList(it)
                }
            }
        }

        binding.refresh.setOnClickListener {
            try {
                val data = viewModel.readOccurrencesWithActivities.value
                data?.forEach {
                    val secondsPassed: Long? =
                        getSecondsPassed(it.occurrenceActivities.last().fullDate)
                    val id = it.occurrenceActivities.last().activityId
                    val owner = it.occurrenceActivities.last().occurrenceOwnerId
                    val fullDate = it.occurrenceActivities.last().fullDate

                    val intervalSeconds =
                        it.occurrenceActivities.last().intervalSeconds

                    val secondsTo = intervalSeconds?.let { it1 ->
                        getSecondsTo(
                            it1,
                            it.occurrenceActivities.last().fullDate
                        )
                    }

                    fun getActivityToUpdate(): Activity {
                        val aktiwiti = Activity(
                            id,
                            owner,
                            fullDate,
                            it.occurrenceActivities.last().timeSpend,
                            secondsPassed,
                            it.occurrenceActivities.last().intervalSeconds,
                            secondsTo
                        )
                        Log.d("To update: ", "$aktiwiti")
                        return aktiwiti
                    }
                    viewModel.updateActivity(getActivityToUpdate())
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "eror: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.occurenciesRecyclerView.layoutManager = LinearLayoutManager(this.context)

        binding.categoryChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val chip = group.findViewById<Chip>(checkedIds.first())
            val selectedCategory = chip.text.toString()

            selectedCategoryChip = selectedCategory
            selectedCategoryChipId = checkedIds.first()

            when (selectedCategoryChip) {
                "All" -> {
                    viewModel.readOccurrencesWithActivities.observe(this.viewLifecycleOwner) { items ->
                        items.let {
                            adapter.submitList(it)
                        }
                    }
                }
                else -> {
                    viewModel.getOccurrencesByCategory(selectedCategoryChip)
                        .observe(this.viewLifecycleOwner) { items ->
                            items.let {
                                adapter.submitList(it)
                            }
                        }
                }
            }
        }
        binding.newOccurency.rippleColor = resources.getColor(R.color.heavenBlue)
        binding.newOccurency.setOnClickListener {
            val action =
                CounterHomeFragmentDirections.actionCounterHomeFragmentToNewFragment("Create new Occurrence")
            this.findNavController().navigate(action)
        }
    }

    private fun makeListOfSecondsTo(it: List<OccurrenceWithActivities>): MutableList<Long> {
        val listOfSeconds = mutableListOf<Long>()
        it.forEach {
            listOfSeconds.add(
                it.occurrenceActivities.last().secondsToNext!!
            )
        }

        return listOfSeconds
    }

    private fun getSecondsTo(secondsTo: Long, lastDateTime: String): Long {
        val pattern = "HH:mm:ss dd.MM.yyyy"
        val formatter = DateTimeFormatter.ofPattern(pattern)
        val lastDate = LocalDateTime.parse(lastDateTime, formatter)
        val calculatedToDay = lastDate.plusSeconds(secondsTo)
        val secondsTo = ChronoUnit.SECONDS.between(
            LocalDateTime.now(),
            calculatedToDay,
        )
        return secondsTo

    }

    private fun getSecondsPassed(lastDateTime: String): Long {
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


}