package com.example.counter.pickers

import android.app.DatePickerDialog
import android.app.TimePickerDialog

//import android.app.DatePickerDialog
//import android.app.Dialog
//import android.app.TimePickerDialog
//import android.content.Context
//import android.os.Bundle
//import android.widget.DatePicker
//import android.widget.TimePicker
//import androidx.fragment.app.DialogFragment
//import androidx.fragment.app.setFragmentResult
//import java.text.SimpleDateFormat
//import java.time.format.DateTimeFormatter
//import java.util.*

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragment: DialogFragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    val c = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(),this,year,month,day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val selectedDate = SimpleDateFormat("dd-MM-yyy").format(c.time)
        val selectedDateBundle = Bundle()
        selectedDateBundle.putString("SELECTED_DATE",selectedDate)

        setFragmentResult("REQUEST_KEY", selectedDateBundle)
        Log.d("Picker","///////////////// onDateSet")

    }


    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        TODO("Not yet implemented")
    }

}