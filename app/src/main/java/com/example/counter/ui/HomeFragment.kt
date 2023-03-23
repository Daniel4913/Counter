package com.example.counter.ui


import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.R
import com.example.counter.adapters.OccurrenceActivitiesListAdapter
import com.example.counter.data.modelentity.EventLog
import com.example.counter.data.relations.EventWithEventLogs
import com.example.counter.databinding.FragmentCounterHomeBinding
import com.example.counter.util.Constants
import com.example.counter.viewmodels.CounterViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
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
                    it.event.eventId
                )
            this.findNavController().navigate(action)
        }

        binding.homeRecyclerView.adapter = adapter
        binding.homeRecyclerView.layoutManager = LinearLayoutManager(this.context)


        //todo 3 razy robie to samo ;// a jeszcze musialbym to zrobic w refresh list.
        // Musze stworzyc funkcje do fetchowania tego


        if (selectedCategoryChip == "All") {
            viewModel.readAllOccurrencesWithActivities.observe(this.viewLifecycleOwner) { items ->
                items.let { occurrencesList ->
                    try {
                        val listSeconds = makeListOfSecondsTo(occurrencesList)
                        val itemsBySeconds =
                            occurrencesList.associateBy { it.singleEventEventLogs.last().secondsToNext }
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
        }


        binding.categoryChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val chip = group.findViewById<Chip>(checkedIds.first())
            val selectedCategory = chip.text.toString()

            selectedCategoryChip = selectedCategory
            selectedCategoryChipId = checkedIds.first()

            when (selectedCategoryChip) {
                "All" -> {
                    viewModel.readAllOccurrencesWithActivities.observe(this.viewLifecycleOwner) { items ->
                        items.let { occurrencesList ->
                            try {
                                val listSeconds = makeListOfSecondsTo(occurrencesList)
                                val itemsBySeconds =
                                    occurrencesList.associateBy { it.singleEventEventLogs.last().secondsToNext }
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
                }
                else -> {
                    viewModel.getOccurrencesByCategory(selectedCategoryChip)
                        .observe(this.viewLifecycleOwner) { items ->
                            items.let { occurrencesList ->
                                try {
                                    val listSeconds = makeListOfSecondsTo(occurrencesList)
                                    val itemsBySeconds =
                                        occurrencesList.associateBy { it.singleEventEventLogs.last().secondsToNext }
                                    val sortedItems =
                                        listSeconds.sorted().map { itemsBySeconds[it] }
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
                }
            }
        }

        binding.newOccurency.setOnClickListener {
            val action =
                HomeFragmentDirections.actionCounterHomeFragmentToNewFragment()
            this.findNavController().navigate(action)
        }
    }

    private fun refreshList() {
        Timber.d("refreshList chip: $selectedCategoryChip")
        val data = viewModel.readAllOccurrencesWithActivities.value
        data?.forEach {
            val lastActivitySeconds = it.singleEventEventLogs.last()
            val occurrence = it.event

            val secondsPassed: Long? =
                getSecondsPassed(it.singleEventEventLogs.last().fullDate)
            val id = it.singleEventEventLogs.last().eventLogId
            val owner = it.singleEventEventLogs.last().eventOwnerId
            val fullDate = it.singleEventEventLogs.last().fullDate

            val intervalSeconds =
                it.singleEventEventLogs.last().intervalSeconds

            val secondsTo = intervalSeconds?.let { it1 ->
                getSecondsTo(
                    it1,
                    it.singleEventEventLogs.last().fullDate
                )
            }

            fun getActivityToUpdate(): EventLog {
                val aktiwiti = EventLog(
                    id,
                    owner,
                    fullDate,
                    secondsPassed,
                    it.singleEventEventLogs.last().intervalSeconds,
                    secondsTo
                )
                return aktiwiti
            }
            viewModel.updateActivity(getActivityToUpdate())
            lastActivitySeconds.intervalSeconds?.let { it1 ->
                viewModel.setEventStatus(
                    lastActivitySeconds.secondsToNext,
                    it1, event = occurrence
                )
            }
        }

    }

    private fun makeListOfSecondsTo(occurrencesList: List<EventWithEventLogs>): MutableList<Long> {
        val listOfSeconds = mutableListOf<Long>()
        occurrencesList.forEach {
            listOfSeconds.add(
                it.singleEventEventLogs.last().secondsToNext!!
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