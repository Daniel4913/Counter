package com.example.counter.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.R
import com.example.counter.adapters.OccurrenceActivitiesListAdapter
//import com.example.counter.data.DataStoreRepository
import com.example.counter.data.modelentity.Activity
import com.example.counter.data.relations.OccurrenceWithActivities
import com.example.counter.databinding.FragmentCounterHomeBinding
import com.example.counter.util.Constants
import com.example.counter.viewmodels.CounterViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var viewModel: CounterViewModel

    private var selectedCategoryChip = "All" //const default
    private var selectedCategoryChipId = 0

    private var _binding: FragmentCounterHomeBinding? = null
    private val binding get() = _binding!!

    private var refreshedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCounterHomeBinding.inflate(inflater, container, false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when (menuItem.itemId) {
                    R.id.occurrences_delete_all -> {
                        viewModel.deleteAllOccurrences()
                    }
                    R.id.home_refresh -> {
                        refreshList()
                    }
                }
                return true

            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = OccurrenceActivitiesListAdapter {
            val action =
                HomeFragmentDirections.actionCounterHomeFragmentToOccurenceFragment(
                    it.occurrence.occurrenceId,
                    it.occurrence.occurrenceName
                )
            this.findNavController().navigate(action)
        }

        binding.occurenciesRecyclerView.adapter = adapter
        viewModel.readOccurrencesWithActivities.observe(this.viewLifecycleOwner) { items ->
            items.let { occurrencesList ->
                try {
                    val listSeconds = makeListOfSecondsTo(occurrencesList)
                    val itemsBySeconds =
                        occurrencesList.associateBy { it.occurrenceActivities.last().secondsToNext }
                    val sortedItems = listSeconds.sorted().map { itemsBySeconds[it] }
                    adapter.submitList(sortedItems)
                    if (refreshedOnce) {
                        refreshList()
                        refreshedOnce = false
                    }
                } catch (e: java.lang.Exception) {
                    Toast.makeText(
                        context,
                        "List will be sorted when all Occurrences have at least one activity",
                        Toast.LENGTH_LONG
                    ).show()
                    adapter.submitList(occurrencesList)
                }

            }
        }

        binding.refresh.setOnClickListener {
            try {
                refreshList()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "eror: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.occurenciesRecyclerView.layoutManager = LinearLayoutManager(this.context)


//        viewModel.readFilterCategory.asLiveData().observe(viewLifecycleOwner) { value ->
//            selectedCategoryChip = value.filteredCategoryChip
//            selectedCategoryChipId = value.filteredCategoryChipId
//
//            updateChip(value.filteredCategoryChipId, binding.categoryChipGroup)
//
//            Log.d("Datastore", "$selectedCategoryChipId $selectedCategoryChip")
//        }

        binding.categoryChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val chip = group.findViewById<Chip>(checkedIds.first())
            val selectedCategory = chip.text.toString()

            selectedCategoryChip = selectedCategory
            selectedCategoryChipId = checkedIds.first()

//            viewModel.saveFilterCategoryTemp(selectedCategoryChip, selectedCategoryChipId)

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


        binding.newOccurency.setOnClickListener {
            val action =
                HomeFragmentDirections.actionCounterHomeFragmentToNewFragment("Create new Occurrence")
            this.findNavController().navigate(action)
        }


    }


    private fun refreshList() {
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
                    secondsPassed,
                    it.occurrenceActivities.last().intervalSeconds,
                    secondsTo
                )
                Log.d("To update: ", "$aktiwiti")
                return aktiwiti
            }
            viewModel.updateActivity(getActivityToUpdate())
        }
    }

    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {
        if (chipId != 0) {
            try {
                val targetView = chipGroup.findViewById<Chip>(chipId)
                targetView.isChecked = true
                chipGroup.requestChildFocus(targetView, targetView)
            } catch (e: java.lang.Exception) {
                Log.d("Home fun updateChip", e.message.toString())
            }
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
        val formatter = DateTimeFormatter.ofPattern(Constants.DEFAULT_FORMATTER)
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
        val formatter = DateTimeFormatter.ofPattern(Constants.DEFAULT_FORMATTER)
        val lastDate = LocalDateTime.parse(lastDateTime, formatter)
        val secondsPassed = ChronoUnit.SECONDS.between(
            lastDate,
            today
        )
        return secondsPassed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        refreshedOnce = false
    }

}