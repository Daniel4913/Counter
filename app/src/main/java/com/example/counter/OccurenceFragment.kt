package com.example.counter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
//import android.os.Build.VERSION_CODES.R to bylo zaimportowane kiedy zjebaly sie stringi (unresolved reference R. string)
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.Constants.Companion.DAYS
import com.example.counter.Constants.Companion.HOURS
import com.example.counter.adapters.DatesTimesListAdapter

import com.example.counter.R.string

import com.example.counter.data.DateTime

import com.example.counter.data.Occurence
import com.example.counter.databinding.FragmentOccurenceBinding
import com.example.counter.services.TimerService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds
import com.example.counter.Constants.Companion.MINUTES
import com.example.counter.Constants.Companion.MONTHS
import com.example.counter.Constants.Companion.WEEKS

class OccurenceFragment : Fragment() {


    private val navigationArgs: OccurenceFragmentArgs by navArgs()

    lateinit var occurence: Occurence

    //    lateinit var datesTimes: LiveData<List<DateTime>> unused
    private lateinit var serviceIntent: Intent

    lateinit var dateTime: DateTime
    lateinit var timePassed: String
    lateinit var lastDateTime: String

    private var minutes = MINUTES
    private var hours = HOURS
    private var days = DAYS
    private var weeks = WEEKS
    private var months = MONTHS


    private var timerStarted = false
    private var time = 0.0
    private var _bindingOccurence: FragmentOccurenceBinding? = null
    private val bindingOccurence get() = _bindingOccurence!!

    private val viewModel: CounterViewModel by viewModels {
        DateTimeViewModelFactory(
            (activity?.application as CounterApplication).database.occurenceDao(),
            (activity?.application as CounterApplication).database.dateTimeDao(),
            (activity?.application as CounterApplication).database.descriptionDao()
        )
    }


    private fun bind(occurence: Occurence) {
        bindingOccurence.apply {
            occurencyName.text = occurence.occurenceName
            occurenceCreateDate.text = occurence.createDate
            occurencyCategory.text = occurence.category
            occurIcon.setImageResource(getOccurIcon())
            deleteBtn.setOnClickListener { showConfirmationDialog() }
            editBtn.setOnClickListener { editOccurence() }
            startTimer.setOnClickListener { startStopTimer() }
            resetTimer.setOnClickListener { resetTimer() }
            intervalTextView.text = occurence.intervalFrequency

//            getSecondsTo()
        }
    }

    private fun getOccurIcon(): Int {
        val occurMore: Boolean = occurence.occurMore
        return if (occurMore) {
            R.drawable.ic_expand_more
        } else {
            R.drawable.ic_expand_less
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.currentOccurence = navigationArgs.id
        viewModel.getOccurenceDatesTimes()
        _bindingOccurence = FragmentOccurenceBinding.inflate(inflater, container, false)
        return bindingOccurence.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.id
        viewModel.retrieveOccurence(id).observe(this.viewLifecycleOwner) { selectedOccurence ->
            occurence = selectedOccurence
            bind(occurence)
        }

        val adapter = DatesTimesListAdapter {
            dateTime = it
            showConfirmationDialogDeleteDateTime()
        }

        bindingOccurence.occurenceDetailRecyclerView.adapter = adapter

        viewModel.retrieveDatesTimes(id).observe(this.viewLifecycleOwner) { selectedOccurenceList ->
            selectedOccurenceList.let {
                adapter.submitList(it as MutableList<DateTime>)
            }
        }

        viewModel.retrieveDatesTimes(id).observe(this.viewLifecycleOwner) { selectedOccurenceList ->
            if (selectedOccurenceList.isEmpty()) {
            } else {
                lastDateTime = selectedOccurenceList[0].fullDate
                bindingOccurence.occurencyTimeFrom.text = secondsToComponents(getSecondsPassed())
            }
        }

        viewModel.retrieveDescriptions(id)
            .observe(this.viewLifecycleOwner) { selectedDescriptionsList ->
                if (selectedDescriptionsList.isEmpty()) {
                    bindingOccurence.desciption.text = "Create description here"
                } else {
                    bindingOccurence.desciption.text = selectedDescriptionsList[0].descriptionNote
                }
            }

        bindingOccurence.descriptionsHolder.setOnClickListener {
            val idOccurence = navigationArgs.id
            val action =
                OccurenceFragmentDirections.actionOccurenceFragmentToDescriptionFragment(idOccurence)
            this.findNavController().navigate(action)
        }

        bindingOccurence.occurenceDetailRecyclerView.layoutManager =
            LinearLayoutManager(this.context)

        // Set button
        bindingOccurence.startActivity.setOnClickListener { addNewDateTime() }

        // start stop timer
        serviceIntent = Intent(context?.applicationContext, TimerService::class.java)
        context?.registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

        //TO NIE DZIALA
        val datesTimesSize = adapter.currentList.size + 1
        bindingOccurence.occurencyTimeTo.text = datesTimesSize.toString()

        if (this::occurence.isInitialized) {
            getSecondsTo()
        }

    }

    // CALCULATING BLOK

    fun getSecondsTo(): Long {
        val interval = occurence.intervalFrequency

        val intervalValue = interval.split(" ")[0].toLong()
        val intervalFrequency = interval.split(" ")[1]
        var toSeconds: Long = 0
        when (intervalFrequency) {
            MINUTES -> toSeconds = 60 * intervalValue
            HOURS -> toSeconds = 3600 * intervalValue
            DAYS -> toSeconds = 86400 * intervalValue
            WEEKS -> toSeconds = 604800 * intervalValue
            MONTHS -> toSeconds = 2629800 * intervalValue
        }
        return toSeconds
    }

    private fun calculateTimeTo(secondsTo: Long): String {

        val durationInterval = secondsTo.seconds
        val durationLastDateTime = lastDateTime.seconds
        val secondsLastDT = ChronoUnit.SECONDS.between()

        val interval = secondsTo
        val pattern = "HH:mm:ss dd.MM.yyyy"
        val formatter = DateTimeFormatter.ofPattern(pattern)
        val lastDate = LocalDateTime.parse(lastDateTime, formatter)
        val secondsPassed = ChronoUnit.SECONDS.between(
            lastDate,
            interval
        )
        return secondsPassed.toString()
    }



    fun getSecondsPassed(): Long {
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

    private fun secondsToComponents(secondsPassed: Long): String {
        secondsPassed.seconds.toComponents { days, hours, minutes, seconds, nanoseconds ->
            val calculated = when (days) {
                0L -> "${hours}h ${minutes}m ${seconds}s"
                else -> "${days}d ${hours}h ${minutes}m ${seconds}s"
            }

            return calculated
        }
    }

    // SRATATATA

    private fun addNewDateTime(timerValue: String = " ") {
        viewModel.addNewDateTime(
            navigationArgs.id,
            getDate(),
            getHour(),
            getHour(),
            timerValue
        )
    }

    private fun getDate(): String {
        return viewModel.getDate()
    }

    private fun getHour(): String {
        return viewModel.getHour()
    }

    /**
     * Deletes tapped date time item on recycler view
     */
    private fun deleteDateTime() {
        viewModel.deleteDateTime(dateTime)
    }

    /**
     * Deletes the current occurence and navigates to the home fragment.
     */
    private fun deleteOccurence() {
        viewModel.deleteOccurence(occurence)
        findNavController().navigateUp()
    }

    private fun editOccurence() {
        val action = OccurenceFragmentDirections.actionOccurenceFragmentToNewFragment(
            "Edit occurence",
            occurence.occurenceId
        )
        this.findNavController().navigate(action)
    }

    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _bindingOccurence = null
    }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(com.example.counter.R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(string.no)) { _, _ -> }
            .setPositiveButton(getString(string.yes)) { _, _ ->
                deleteOccurence()
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
                addNewDateTime(timePassed)
            }
            .show()
    }


    /**
     * Timer block for measure how long singgle occurence-activity was
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

