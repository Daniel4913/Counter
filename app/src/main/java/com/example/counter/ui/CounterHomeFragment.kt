package com.example.counter.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.adapters.OccurrenceActivitiesListAdapter
import com.example.counter.data.modelentity.Activity
import com.example.counter.databinding.FragmentCounterHomeBinding
import com.example.counter.viewmodels.CounterViewModel
import com.example.counter.viewmodels.TimeCounter


import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

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


        val data = viewModel.readOccurrencesWithActivities.value
        data?.forEach {
            if (it.occurrenceActivities.isNotEmpty()) {

                val timeCounter = TimeCounter(
                    it.occurrence,
                    it.occurrenceActivities[0]
                )

                var secondsPassed: Long? = timeCounter.getSecondsPassed()

                val id = it.occurrenceActivities[0].dateTimeId
                val owner = it.occurrenceActivities[0].occurrenceOwnerId
                val fullDate = it.occurrenceActivities[0].fullDate


                val intervalSeconds =
                    it.occurrenceActivities[0].intervalSeconds
                val secondsTo = intervalSeconds?.minus(secondsPassed!!)

                fun getActivityToUpdate(): Activity {
                    val aktiwiti = Activity(
                        id,
                        owner,
                        fullDate,
                        it.occurrenceActivities[0].timeSpend,
                        secondsPassed,
                        it.occurrenceActivities[0].intervalSeconds,
                        secondsTo
                    )
                    Log.d("Home", "$aktiwiti")
                    return aktiwiti
                }
                binding.refresh.setOnClickListener {
                    viewModel.updateActivity(getActivityToUpdate())
                    Log.d("hOMEE", "UpdateActivityCalled")
                }
            }
        }

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

            items.let {

                val sorted = items.sortedBy { it.occurrenceActivities[0].secondsToNext }
                adapter.submitList(sorted)

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
                            it as MutableList
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


        binding.newOccurency.setOnClickListener {
            val action =
                CounterHomeFragmentDirections.actionCounterHomeFragmentToNewFragment("Create new occurence")
            this.findNavController().navigate(action)
        }
    }

}