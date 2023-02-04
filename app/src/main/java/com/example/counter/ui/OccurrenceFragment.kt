package com.example.counter.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.counter.services.TimerService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt
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
    private lateinit var serviceIntent: Intent
    private lateinit var occurrence: Occurrence
    private lateinit var activity: Activity // counter.data.modelentity.Activity
    private lateinit var timePassed: String
    private lateinit var lastActivity: String

    private var timerStarted = false
    private var time = 0.0
    private var _bindingOccurence: FragmentOccurenceBinding? = null
    private val bindingOccurence get() = _bindingOccurence!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
    }

    private fun bind(occurrence: Occurrence) {
        bindingOccurence.apply {
            occurencyName.text = occurrence.occurrenceName
            occurenceCreateDate.text = occurrence.createDate
            occurencyCategory.text = occurrence.category
            occurIcon.setImageResource(getOccurIcon())
            deleteBtn.setOnClickListener { showConfirmationDialog() }
            editBtn.setOnClickListener { editOccurrence() }
            startTimer.setOnClickListener { startStopTimer() }
            resetTimer.setOnClickListener { resetTimer() }
            intervalTextView.text = occurrence.intervalFrequency
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.getOccurrenceWithActivities()
        _bindingOccurence = FragmentOccurenceBinding.inflate(inflater, container, false)

        return bindingOccurence.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.id

        viewModel.getOccurrence(id).observe(this.viewLifecycleOwner) { selectedOccurence ->
            occurrence = selectedOccurence
            bind(occurrence)
        }

        val adapter = ActivitiesListAdapter {
            activity = it
//            showConfirmationDialogDeleteDateTime()
            val action = OccurrenceFragmentDirections.actionOccurenceFragmentToActivityEditFragment(it.activityId)
            this.findNavController().navigate(action)

        }

        bindingOccurence.occurenceDetailRecyclerView.adapter = adapter

        viewModel.getActivities(id).observe(this.viewLifecycleOwner) { selectedOccurrenceList ->
            selectedOccurrenceList.let {
                adapter.submitList(it as MutableList<Activity>)
            }
            if (this::occurrence.isInitialized && selectedOccurrenceList.isNotEmpty()) {
                lastActivity = selectedOccurrenceList[0].fullDate

                bindingOccurence.occurencyTimeFrom.text =
                    secondsToComponents(getSecondsPassed())

                bindingOccurence.occurencyTimeTo.text =
                    secondsToComponents(getSecondsTo(getIntervalSeconds()))

                val datesTimesSize = selectedOccurrenceList.size
                bindingOccurence.listSizeTextView.text =
                    datesTimesSize.toString()

                val timeString = bindingOccurence.occurencyTimeTo.text

                updateTimeColor(timeString)
            }
        }

//        bindingOccurence.descriptionsHolder.setOnClickListener {
//            val idOccurence = navigationArgs.id
//            val action =
//                OccurenceFragmentDirections.actionOccurenceFragmentToDescriptionFragment(idOccurence)
//            this.findNavController().navigate(action)
//        }

        bindingOccurence.occurenceDetailRecyclerView.layoutManager =
            LinearLayoutManager(this.context)

        // Set button
        bindingOccurence.addActivity.setOnClickListener { addNewActivity() }

        // start stop timer
        serviceIntent = Intent(context?.applicationContext, TimerService::class.java)
        context?.registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

    }

    private fun updateTimeColor(timeString: CharSequence) {

        if (timeString.contains("-")) {
            bindingOccurence.occurencyTimeToLabel.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red_700
                )
            )
            bindingOccurence.occurencyTimeTo.setTextColor(
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
            bindingOccurence.occurencyTimeToLabel.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.orange
                )
            )
            bindingOccurence.occurencyTimeTo.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.orange
                )
            )
        } else {

            bindingOccurence.occurencyTimeToLabel.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                )
            )
            bindingOccurence.occurencyTimeTo.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                )
            )
        }
    }

    private fun getOccurIcon(): Int {
        val occurMore: Boolean = occurrence.occurMore
        return if (occurMore) {
            R.drawable.ic_expand_more
        } else {
            R.drawable.ic_expand_less
        }
    }

    // CALCULATING BLOK

    fun getIntervalSeconds(): Long {
        val interval = occurrence.intervalFrequency

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

    // DB SRATATATA

    private fun addNewActivity(timerValue: String = " ") {
        viewModel.addNewActivity(
            navigationArgs.id,
            getDate(),
            getHour(),
            0,
            getIntervalSeconds(),
            0
        )
    }

    private fun getDate(): String {
        return viewModel.getDate()
    }

    private fun getHour(): String {
        return viewModel.getHour()
    }

    private fun deleteDateTime() {
        viewModel.deleteActivity(activity)
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
        _bindingOccurence = null
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

    private fun showConfirmationDialogDeleteDateTime() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(string.delete_dateTime_question))
            .setCancelable(false)
            .setNegativeButton(getString(string.no)) { _, _ -> }
            .setPositiveButton(getString(string.yes)) { _, _ ->
                deleteDateTime()
            }
            .show()
    }

    private fun showSaveTimeDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(string.save_timer_question, timePassed))
            .setCancelable(false)
            .setNegativeButton(getString(string.no)) { _, _ -> }
            .setPositiveButton(getString(string.yes)) { _, _ ->
                addNewActivity(timePassed)
            }
            .show()
    }


    /**
     * Timer block for measure how long occurrence activity was
     */
    // https://www.youtube.com/watch?v=LPjhP9D3pm8
    private fun resetTimer() {
        timePassed = getTimeStringFromDouble(time)
        showSaveTimeDialog()
        stopTimer()
        time = 0.0
        bindingOccurence.timerCounter.text = getTimeStringFromDouble(time)
    }

    private fun startStopTimer() {
        if (timerStarted)
            stopTimer()
        else
            startTimer()
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        context?.startService(serviceIntent)
        bindingOccurence.startTimer.text = "stop"
        timerStarted = true
    }

    private fun stopTimer() {
        context?.stopService(serviceIntent)
        bindingOccurence.startTimer.text = "start"
        timerStarted = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            bindingOccurence.timerCounter.text = getTimeStringFromDouble(time)

        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val hrs = resultInt % 86400 / 3600
        val min = resultInt % 86400 % 3600 / 60
        val sec = resultInt % 86400 % 3600 % 60
        return makeTimeString(hrs, min, sec)
    }

    private fun makeTimeString(hrs: Int, mins: Int, sec: Int): String =
        String.format("%02d:%02d:%02d", hrs, mins, sec)
}

