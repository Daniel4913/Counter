package com.example.counter.pickers

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.text.DateFormat.getTimeInstance
import java.time.LocalDateTime

import java.time.format.DateTimeFormatter
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    val c = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return TimePickerDialog(requireContext(), this, hourOfDay, minute, is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)

        fun selectedTimeToString(hourOfDay: Int, minute: Int): String {
            return "${hourOfDay}:${minute}"
        }

        val selectedDateBundle = Bundle()
        selectedDateBundle.putString("SELECTED_TIME", selectedTimeToString(hourOfDay, minute))

        setFragmentResult("TIME_KEY", selectedDateBundle)
    }
}