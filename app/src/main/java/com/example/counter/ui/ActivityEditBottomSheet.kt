package com.example.counter.ui

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.counter.R
import com.example.counter.data.modelentity.EventLog
import com.example.counter.databinding.ActivityEditBottomSheetBinding
import com.example.counter.util.Constants.Companion.DEFAULT_FORMATTER
import com.example.counter.viewmodels.CounterViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class ActivityEditBottomSheet : BottomSheetDialogFragment() {


    private val navigationArgs: ActivityEditBottomSheetArgs by navArgs()

    private lateinit var viewModel: CounterViewModel
    lateinit var eventLog: EventLog
    lateinit var getDate: String
    lateinit var getHour: String

    private var _binding: ActivityEditBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
        super.onCreate(savedInstanceState)
    }

    private fun bind(eventLog: EventLog) {
        binding.apply {
            dateTextInputLayout.hint = splitFullDate()[1]
            dateEditText.setText(splitFullDate()[1])
            hourTextInputLayout.hint = splitFullDate()[0]
            hourEditText.setText(splitFullDate()[0])
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityEditBottomSheetBinding.inflate(layoutInflater, container, false)
        getDate = binding.dateEditText.text.toString()
        getHour = binding.hourEditText.text.toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.id
        viewModel.getEventLog(id).observe(this.viewLifecycleOwner) { receivedActivity ->
            eventLog = receivedActivity
            bind(eventLog)
        }
        
        binding.saveButton.setOnClickListener {
            if (validateDateTime()) {
                viewModel.updateActivity(
                    EventLog(
                        eventLog.eventLogId,
                        eventLog.eventOwnerId,
                        konkatenacjaFullDate(
                            binding.dateEditText.text.toString(),
                            binding.hourEditText.text.toString()
                        ),
                        eventLog.secondsPassed,
                        eventLog.intervalSeconds,
                        eventLog.secondsToNext,
                    )
                )
                requireActivity().onBackPressedDispatcher.onBackPressed()
            } else {
                Toast.makeText(context, "Date or hour format is invalid", Toast.LENGTH_SHORT).show()
            }
        }

        binding.deleteButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            viewModel.deleteEventLog(
                EventLog(
                    eventLog.eventLogId,
                    eventLog.eventOwnerId,
                    eventLog.fullDate,
                    eventLog.secondsPassed,
                    eventLog.intervalSeconds,
                    eventLog.secondsToNext
                )
            )
        }
    }


    private fun validateDateTime(): Boolean {
        val formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMATTER)
        val checkThis = konkatenacjaFullDate(
            binding.dateEditText.text.toString(),
            binding.hourEditText.text.toString()
        )
        try {
            LocalDateTime.parse(checkThis, formatter)
            return true
        } catch (e: java.lang.Exception) {
            Log.d("BottomSheet validateDateTime", e.message.toString())
        }
        return false
    }

    private fun konkatenacjaFullDate(date: String, hour: String): String {
        Log.d("Edited activity", "$hour $date")
        return "$hour $date"
    }

    private fun splitFullDate(): List<String> {
        val createDate = eventLog.fullDate
        return createDate.split(" ")
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        //nie dziala
        val fragmentTitle = getString(R.string.fragmentTitle, navigationArgs.occurrenceTitle)
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = fragmentTitle
        Timber.d("requireactivity: ${requireActivity()}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}