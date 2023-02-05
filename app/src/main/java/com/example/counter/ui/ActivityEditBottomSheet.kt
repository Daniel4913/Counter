package com.example.counter.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.counter.data.modelentity.Activity
import com.example.counter.databinding.ActivityEditBottomSheetBinding
import com.example.counter.util.Constants.Companion.DEFAULT_FORMATTER
import com.example.counter.viewmodels.CounterViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class ActivityEditBottomSheet : BottomSheetDialogFragment() {

    private val navigationArgs: ActivityEditBottomSheetArgs by navArgs()

    private lateinit var viewModel: CounterViewModel
    lateinit var activity: Activity
    lateinit var getDate: String
    lateinit var getHour: String

    private var _binding: ActivityEditBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
        super.onCreate(savedInstanceState)
    }

    private fun bind(activity: Activity) {

        binding.apply {
            dateTextInputLayout.hint = splitFullDate()[1]
            dateEditText.setText(splitFullDate()[1])
            hourTextInputLayout.hint = splitFullDate()[0]
            hourEditText.setText(splitFullDate()[0])
            timeSpendTextInputLayout.hint = activity.timeSpend
            timeSpendEditText.setText(activity.timeSpend)
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
        viewModel.getActivity(id).observe(this.viewLifecycleOwner) { receivedActivity ->
            activity = receivedActivity
            bind(activity)
        }

        binding.dateEditText.setOnClickListener {
            Log.d("EDIT", "$getDate $getHour")
            Log.d(
                "EDIT", konkatenacjaFullDate(
                    binding.dateEditText.text.toString(),
                    binding.hourEditText.text.toString()
                )
            )
        }

        binding.saveButton.setOnClickListener {
            if (validateDateTime()) {
                viewModel.updateActivity(
                    Activity(
                        activity.activityId,
                        activity.occurrenceOwnerId,
                        ////////                        ////////
                        konkatenacjaFullDate(
                            binding.dateEditText.text.toString(),
                            binding.hourEditText.text.toString()
                        ), //parse exception przy zlej kolejnosci data-godzina!!!
//                    binding.timeSpendEditText.text.toLong()
                        // //////                        ////////
                        activity.timeSpend,
                        activity.secondsPassed,
                        activity.intervalSeconds,
                        activity.secondsToNext,
                    )
                )
            } else {
                Toast.makeText(context, "Date or hour format is invalid", Toast.LENGTH_SHORT).show()
            }
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
        val createDate = activity.fullDate
        return createDate.split(" ")
    }
}