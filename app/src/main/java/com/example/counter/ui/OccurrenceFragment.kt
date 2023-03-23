package com.example.counter.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.adapters.ActivitiesListAdapter
import com.example.counter.R.string
import com.example.counter.data.modelentity.EventLog
import com.example.counter.data.modelentity.Event
import com.example.counter.databinding.FragmentOccurenceBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.counter.R
import com.example.counter.data.modelentity.Category
import com.example.counter.data.modelentity.CounterStatus
import com.example.counter.util.TimeCounter
import com.example.counter.util.TimeCounter.Companion.calculateIntervalToSeconds
import com.example.counter.viewmodels.CounterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.properties.Delegates

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class OccurrenceFragment : Fragment() {

    private val navigationArgs: OccurrenceFragmentArgs by navArgs()

    private lateinit var viewModel: CounterViewModel
    private lateinit var event: Event
    private lateinit var lastEventLog: EventLog

    private var intervalSeconds by Delegates.notNull<Long>()

    private var _binding: FragmentOccurenceBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]

    }

    private fun bind(event: Event) {
        binding.apply {
            occurenceCreateDate.text = event.createDate
            occurrencyCategory.text = event.category.name
            categoryIcon.setImageResource(Category.valueOf(event.category.name).icon)
            occurrenceIcon.text = event.eventIcon
            intervalTextView.text = event.intervalFrequency
            addActivity.setOnClickListener { addNewEventLog() }
            divider.setBackgroundColor(Category.valueOf(event.category.name).underscoreColor)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOccurenceBinding.inflate(inflater, container, false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.occurrence_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.occurrence_delete -> {
                        showConfirmationDialog()
                    }
                    R.id.occurrence_edit -> {
                        editOccurrence()
                    }
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val occurrenceId = navigationArgs.id

        viewModel.getOccurrence(occurrenceId)
            .observe(this.viewLifecycleOwner) { selectedEvent ->
                event = selectedEvent
                bind(selectedEvent)

                val fragmentTitle = getString(string.fragmentTitle, event.eventName)
                (activity as? AppCompatActivity)?.supportActionBar?.title = fragmentTitle

            }


        val adapter = ActivitiesListAdapter(
            { activity ->
                val action =
                    OccurrenceFragmentDirections.actionOccurenceFragmentToActivityEditFragment(
                        activity.eventLogId, event.eventName
                    )
                this.findNavController().navigate(action)
            },
            { activity ->
                viewModel.deleteEventLog(activity)
            }
        )

        binding.occurrenceDetailsRecyclerView.adapter = adapter
        binding.occurrenceDetailsRecyclerView.layoutManager =
            LinearLayoutManager(this.context)

        viewModel.getActivities(occurrenceId).observe(viewLifecycleOwner) { occurrenceActivities ->
            occurrenceActivities.let {
                adapter.submitList(it)
            }
            if (occurrenceActivities.isEmpty()) {
                binding.occurrenceTimeFrom.text = ""
                binding.occurrenceTimeTo.text = ""
                binding.listSizeTextView.text = "0"
            }

            if (
                this::event.isInitialized &&
                occurrenceActivities.isNotEmpty()
            ) {
                val countTime = TimeCounter(event, occurrenceActivities.last())
                intervalSeconds = countTime.getIntervalSeconds!!
                val timeFrom = countTime.getSecondsPassed()
                val timeTo = countTime.getSecondsTo(intervalSeconds)
                binding.occurrenceTimeFrom.text = countTime.secondsToComponents(timeFrom)
                binding.occurrenceTimeTo.text = countTime.secondsToComponents(timeTo)

                val datesTimesSize = occurrenceActivities.size
                binding.listSizeTextView.text =
                    datesTimesSize.toString()

                event.status?.let { applyTimeColor(it) }
                lastEventLog = occurrenceActivities.last()
            } else {
                binding.progressBar.visibility = View.VISIBLE
                binding.progressBar2.visibility = View.VISIBLE
                Toast.makeText(
                    requireContext(),
                    "Error occurred while calculating time",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun applyTimeColor(status: CounterStatus) {
        val context = binding.occurrenceTimeTo.context
        when (status.name) {
            "Enough" -> {
                binding.occurrenceTimeTo.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
            }
            "Late" -> {
                binding.occurrenceTimeTo.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red
                    )
                )
            }
            "CloseTo" -> {
                binding.occurrenceTimeTo.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.orange
                    )
                )
            }
        }
    }

    // database operations

    private fun addNewEventLog() {
        binding.progressBar.visibility = View.INVISIBLE
        binding.progressBar2.visibility = View.INVISIBLE
        viewModel.addNewEventLog(
            navigationArgs.id,
            getDate(),
            0,
            calculateIntervalToSeconds(event.intervalFrequency),
            0
        )
    }

    private fun getDate(): String {
        return viewModel.getDate()
    }

    private fun deleteOccurrence() {
        viewModel.deleteOccurrence(event)
        findNavController().navigateUp()
    }

    private fun editOccurrence() {
        val action = OccurrenceFragmentDirections.actionOccurenceFragmentToNewFragment(
            event.eventId,
            if (this::lastEventLog.isInitialized) lastEventLog.eventLogId else -1
        )
        this.findNavController().navigate(action)
    }

    // dialog for delete
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(string.no)) { _, _ -> }
            .setPositiveButton(getString(string.yes)) { _, _ ->
                deleteOccurrence()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

