package com.example.counter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.adapters.ActivitiesListAdapter
import com.example.counter.R.string
import com.example.counter.data.modelentity.Activity
import com.example.counter.data.modelentity.Occurrence
import com.example.counter.databinding.FragmentOccurenceBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.seconds
import com.example.counter.R
import com.example.counter.util.Constants
import com.example.counter.viewmodels.CounterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class OccurrenceFragment : Fragment() {

    private val navigationArgs: OccurrenceFragmentArgs by navArgs()

    private lateinit var viewModel: CounterViewModel
    private lateinit var occurrence: Occurrence
    private lateinit var lastActivity: String

    private var _binding: FragmentOccurenceBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
    }

    private fun bind(occurrence: Occurrence) {
        binding.apply {
            occurencyName.text = occurrence.occurrenceName
            occurenceCreateDate.text = occurrence.createDate
            occurencyCategory.text = occurrence.category
            deleteBtn.setOnClickListener { showConfirmationDialog() }
            editBtn.setOnClickListener { editOccurrence() }
            intervalTextView.text = occurrence.intervalFrequency
            addActivity.setOnClickListener { addNewActivity() }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOccurenceBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.id

        viewModel.getOccurrence(id).observe(this.viewLifecycleOwner) { selectedOccurrence ->
            occurrence = selectedOccurrence
            bind(selectedOccurrence)
        }

        val adapter = ActivitiesListAdapter(
            { activity ->
                val action =
                    OccurrenceFragmentDirections.actionOccurenceFragmentToActivityEditFragment(
                        activity.activityId
                    )
                this.findNavController().navigate(action)
            },
            { activity ->
                viewModel.deleteActivity(activity)
            }
        )

        binding.occurenceDetailRecyclerView.adapter = adapter
        binding.occurenceDetailRecyclerView.layoutManager =
            LinearLayoutManager(this.context)

        viewModel.getActivities(id).observe(viewLifecycleOwner) { occurrenceActivities ->
            occurrenceActivities.let {
                adapter.submitList(it as MutableList<Activity>)
            }
            if (
                this::occurrence.isInitialized &&
                occurrenceActivities.isNotEmpty()
            ) {
                lastActivity = occurrenceActivities[0].fullDate

                binding.occurencyTimeFrom.text =
                    secondsToComponents(getSecondsPassed())

                binding.occurencyTimeTo.text =
                    secondsToComponents(getSecondsTo(getIntervalSeconds(occurrence.intervalFrequency)))

                val datesTimesSize = occurrenceActivities.size
                binding.listSizeTextView.text =
                    datesTimesSize.toString()

                val timeString = binding.occurencyTimeTo.text

                updateTimeColor(timeString)
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

        Toast.makeText(
            requireContext(),
            "One click to edit activity. Click long to delete ",
            Toast.LENGTH_SHORT
        ).show()

    }

    private fun updateTimeColor(timeString: CharSequence) {

        if (timeString.contains("-")) {
            binding.occurencyTimeToLabel.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red_700
                )
            )
            binding.occurencyTimeTo.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red_700
                )
            )
        } else if (
            timeString.contains("0h")
            || timeString.contains("1h")
            && !timeString.contains("-")

        ) {
            binding.occurencyTimeToLabel.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.orange
                )
            )
            binding.occurencyTimeTo.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.orange
                )
            )
        } else {

            binding.occurencyTimeToLabel.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                )
            )
            binding.occurencyTimeTo.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                )
            )
        }
    }

    // CALCULATING BLOK

    fun getIntervalSeconds(interval: String): Long {

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
        val timeFrom = lastActivity
        val timeTo = secondsTo
        val formatter = DateTimeFormatter.ofPattern(Constants.DEFAULT_FORMATTER)
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
        val formatter = DateTimeFormatter.ofPattern(Constants.DEFAULT_FORMATTER)
        val lastDate = LocalDateTime.parse(lastActivity, formatter)
        val secondsPassed = ChronoUnit.SECONDS.between(
            lastDate,
            today
        )
        return secondsPassed
    }

    fun secondsToComponents(secondsPassed: Long): String {
        secondsPassed.seconds.toComponents { days, hours, minutes, seconds, nanoseconds ->
            var calculated = ""

            when (days) {
                0L -> calculated = "${hours}h ${minutes}m ${seconds}s"
                else -> calculated = "${days}d ${hours}h ${minutes}m ${seconds}s"
            }

            //TODO
//             when (hours) {
//                0 -> calculated = "${minutes}m ${seconds}m "
//                else -> calculated =  "${hours}h ${minutes}m"
//            }

            return calculated
        }
    }

    private fun addNewActivity() {
        viewModel.addNewActivity(
            navigationArgs.id,
            getDate(),
            0,
            getIntervalSeconds(occurrence.intervalFrequency),
            0
        )
    }

    private fun getDate(): String {
        return viewModel.getDate()
    }

    private fun getHour(): String {
        return viewModel.getHour()
    }

    private fun deleteOccurrence() {
        viewModel.deleteOccurence(occurrence)
        findNavController().navigateUp()
    }

    private fun editOccurrence() {
        val action = OccurrenceFragmentDirections.actionOccurenceFragmentToNewFragment(
            "Edit Occurrence",
            occurrence.occurrenceId
        )
        this.findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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

}

