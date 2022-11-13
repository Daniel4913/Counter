package com.example.counter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.adapters.DatesTimesListAdapter
import com.example.counter.data.DateTime
import com.example.counter.data.Occurence
import com.example.counter.databinding.DatesTimesItemBinding
import com.example.counter.databinding.FragmentOccurenceBinding
import com.example.counter.services.TimerService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.roundToInt

class OccurenceFragment : Fragment() {

    private val navigationArgs: OccurenceFragmentArgs by navArgs()

    lateinit var occurence: Occurence
    lateinit var datesTimes: LiveData<List<DateTime>>
    lateinit var dateTime: DateTime
    lateinit var timePassed: String //
    lateinit var totalTimes: String
    private lateinit var serviceIntent: Intent
    private var timerStarted = false
    private var time = 0.0
    private var _bindingOccurence: FragmentOccurenceBinding? = null
    private val bindingOccurence get() = _bindingOccurence!!

    private var _bindingDatesTimes: DatesTimesItemBinding? = null
    private val bindingDatesTimes get() = _bindingDatesTimes!!

    private val viewModel: CounterViewModel by viewModels {
        DateTimeViewModelFactory(
            (activity?.application as CounterApplication).database.occurenceDao(),
            (activity?.application as CounterApplication).database.dateTimeDao()
        )
    }

    private fun bind(occurence: Occurence) {
        bindingOccurence.apply {
            occurencyName.text = occurence.occurenceName
            occurenceCreateDate.text = occurence.createDate
            occurencyCategory.text = occurence.category
            deleteBtn.setOnClickListener { showConfirmationDialog() }
            startTimer.setOnClickListener { startStopTimer() }
            resetTimer.setOnClickListener { resetTimer() }
//            editBtn.setOnClickListener { occurencyTimeFrom.text = getLastDateTime().toString() }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.currentOccurence = navigationArgs.id
        viewModel.getCurrentOccurence()
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
        ////CHRONO UTNIT

//        val date1 = LocalDateTime.parse("2022-30-10 T12:00:00")
//        val date2 = LocalDateTime.parse("2022-03-11 T20:00:00")

//        val date1 = LocalDateTime.now()
//        val date2 = LocalDateTime.of(2022,Month.OCTOBER,30,11,0,0)
//        val date3 = LocalDateTime.of(2022,Month.OCTOBER,30,11,0,0)


//        val date3 = LocalDateTime.of()



//        println("ChronoUnit.DAYS.between(date1, date2) ${ChronoUnit.DAYS.between(date2, date1)}")
//        println("Duration.between(date1, date2).toDays() ${Duration.between(date1, date2).toDays()}")
//        println("date1.until(date2, ChronoUnit.DAYS) ${date1.until(date2, ChronoUnit.DAYS)}")





////////////////////

        //Here is the age String in format to  parse
        //Here is the age String in format to  parse
//        val age = "P17Y9M5D"

        // Converting strings into period value
        // using parse() method

        // Converting strings into period value
        // using parse() method
//        val p = Period.parse(age)
//        println("the age is: ")
//        println(" ${p.years} +  Years\n + ${p.months} +  Months\n + ${p.days} +  Days\n"
//        )

//////////////// kek
        datesTimes = viewModel.getCurrentOccurence()
        val adapter = DatesTimesListAdapter {
            dateTime = it
             showConfirmationDialogDeleteDateTime()}
        bindingOccurence.occurenceDetailRecyclerView.adapter = adapter
        viewModel.retrieveDatesTimes(id).observe(this.viewLifecycleOwner) { selectedOccurence ->
            selectedOccurence.let {
                adapter.submitList(it as MutableList<DateTime>?)
            }
        }
        bindingOccurence.occurenceDetailRecyclerView.layoutManager =
            LinearLayoutManager(this.context)
        // set totalTimes
        fun  setTotalTimes() {
            bindingOccurence.totalTimes.text = adapter.itemCount.toString() // pokazuje 0 :')
            totalTimes = adapter.itemCount.toString()
        }
        // Set button
        bindingOccurence.startActivity.setOnClickListener { addNewDateTime(); setTotalTimes()  }
        // start stop timer
        serviceIntent = Intent(context?.applicationContext, TimerService::class.java )
        context?.registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))


        // get string from last item in recyclerview's adapter
//        val lastDateTimeOnList = bindingOccurence.occurenceDetailRecyclerView.layoutManager?.findViewByPosition(4)
//        Log.d("LastDateTime??????????", lastDateTime.toString()) Unable to instantiate fragment com.example.counter.OccurenceFragment: calling Fragment constructor caused an exception
//        Log.d("LastDateTime??????????", lastDateTimeOnList.toString()) null

//        val lastDateTimeOnList = adapter.currentList.first().fullDate
//        Log.d("LastDateTime??????????", lastDateTimeOnList.toString())  // LIST IS EMPTY

    }

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
     * Deletes the current occurence and navigates to the list fragment.
     */
    private fun deleteOccurence() {
        viewModel.deleteOccurence(occurence)
        findNavController().navigateUp()
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
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteOccurence()
            }
            .show()
    }

    private fun showConfirmationDialogDeleteDateTime() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_dateTime_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteDateTime()
            }
            .show()
    }
    private fun showSaveTimeDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.save_timer_question, timePassed))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
            addNewDateTime(timePassed)
            }
            .show()
    }


    /**
     * Counting bloc, to calculate how much time passed between occurences
     */

    //todo List<DatesTimes[last in recycler list]>
    // get last item from list (last date from dates)
    // chyba nie moge wyciagnac z recycler view tylko musze z db??

//    private fun getLastDateTime() {
//        val id = navigationArgs.id
//        val fullDateTime = viewModel.retrieveLastDateTime(id)
//        Log.d("Full date time", fullDateTime.toString())
//    }

    //    val lastDateTime = bindingOccurence.occurenceDetailRecyclerView.layoutManager.

    // parse date to LocalDateTime

    //get LocalDateTime.now()

    //calculate time between dates to know how much time passed from last actiity



    /**
     * Timer block for measure how long occurence was
     */
    // https://www.youtube.com/watch?v=LPjhP9D3pm8
    private fun resetTimer() {
        timePassed =getTimeStringFromDouble(time)
        showSaveTimeDialog()
        stopTimer()
        time = 0.0
        bindingOccurence.timerCounter.text = getTimeStringFromDouble(time)
    }

    private fun startStopTimer() {
        if(timerStarted)
            stopTimer()
        else
            startTimer()
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        context?.startService(serviceIntent)
        bindingOccurence.startTimer.text = "stop"
        timerStarted=true
            }

    private fun stopTimer() {
        context?.stopService(serviceIntent)
        bindingOccurence.startTimer.text = "start"
        timerStarted=false
    }
    private val updateTime: BroadcastReceiver = object: BroadcastReceiver() {
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
        return  makeTimeString(hrs,min,sec)
    }

    private fun makeTimeString(hrs: Int, mins: Int, sec: Int): String = String.format("%02d:%02d:%02d", hrs, mins,sec)
}

