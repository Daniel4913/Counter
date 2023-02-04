package com.example.counter.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.counter.data.modelentity.Activity
import com.example.counter.databinding.ActivityEditBottomSheetBinding
import com.example.counter.viewmodels.CounterViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

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
    ): View? {

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
            Log.d("EDIT", konkatenacjaFullDate(
                binding.dateEditText.text.toString(),
                binding.hourEditText.text.toString())
            )


        }

        binding.saveButton.setOnClickListener {

            viewModel.updateActivity(

                Activity(
                    activity.activityId,
                    activity.occurrenceOwnerId,
                    konkatenacjaFullDate(
                        binding.dateEditText.text.toString(),
                        binding.hourEditText.text.toString()), //parse exception przy zlej kolejnosci data-godzina!!!
//                    binding.timeSpendEditText.text.toLong()
                    activity.timeSpend
                    ,
                    activity.secondsPassed,
                    activity.intervalSeconds,
                    activity.secondsToNext,
                )

            )
        }

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