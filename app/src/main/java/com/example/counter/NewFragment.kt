package com.example.counter

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.counter.Constants.Companion.DEFAULT_DAYS
import com.example.counter.Constants.Companion.DEFAULT_FREQUENCE
import com.example.counter.Constants.Companion.DEFAULT_HOURS
import com.example.counter.Constants.Companion.DEFAULT_MAX_DAYS
import com.example.counter.Constants.Companion.DEFAULT_MAX_HOURS
import com.example.counter.Constants.Companion.DEFAULT_MINUTES
import com.example.counter.data.Occurence
import com.example.counter.databinding.FragmentNewBinding
import com.example.counter.pickers.DatePickerFragment
import com.example.counter.pickers.TimePickerFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.*


class NewFragment : Fragment() {
    val navigationArgs: NewFragmentArgs by navArgs()

    private val viewModel: CounterViewModel by viewModels {
        DateTimeViewModelFactory(
            (activity?.application as CounterApplication).database.occurenceDao(),
            (activity?.application as CounterApplication).database.dateTimeDao(),
            (activity?.application as CounterApplication).database.descriptionDao()
        )
    }


    private var intervalFrequencyChip = DEFAULT_FREQUENCE
    private var intervalFrequencyChipId = 0
    private var intervalValue = DEFAULT_HOURS

    lateinit var occurence: Occurence
//    private var currentOccurence: Occurence? = occurence

    private var _binding: FragmentNewBinding? = null
    private val binding get() = _binding!!

    private fun bind(occurence: Occurence) {
        binding.apply {
            occurenceName.setText(occurence.occurenceName, TextView.BufferType.SPANNABLE)
            categoryDropdown.setText(occurence.category, TextView.BufferType.SPANNABLE)
            addBtn.setOnClickListener { updateOccurence() }
            tvDate.setText(splitCreateDate()[0])
            tvTime.setText(splitCreateDate()[1])
            intervalNumberPicker.minValue = DEFAULT_HOURS
            intervalNumberPicker.maxValue = DEFAULT_MAX_HOURS
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.occurenceId
        if (id > 0) {
            viewModel.retrieveOccurence(id).observe(this.viewLifecycleOwner) { selectedOccurence ->
                occurence = selectedOccurence
                bind(occurence)
            }
        } else {
            binding.addBtn.setOnClickListener { addNewOccurence() }
        }

        setIntervalValue()


        binding.tvDate.setOnClickListener { getDate() }

        binding.tvTime.setOnClickListener { getTime() }

        val duration = Toast.LENGTH_LONG
        val text = "Click on date and time to change it"
        val toast = Toast.makeText(requireContext(), text, duration)
        toast.show()

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val now = "${hourOfDay}:${minute}"

        if (binding.tvTime.text == now) {
        } else {
            binding.tvTime.setText("${hourOfDay}:${minute}")
            binding.tvDate.setText("${year}-${month}-${day}")
        }


        //TODO update chip gdy edytujemy
//if (currentOccurence != null) {
//    currentOccurence?.occurenceId?.let {
//        viewModel.retrieveOccurence(it).observe(viewLifecycleOwner) { value ->
//            intervalFrequencyChip =
//                value.intervalFrequency //todo wyciagnac tylko czestotliwosc dzien/godzina itd
//            Log.d("intervalFrequencyChip from retrieve occurence", value.intervalFrequency)
//            updateChip(intervalFrequencyChipId, binding.frequencyChipGroup)
//            //TODO FATAL EXCEPTION: lateinit property occurence has not been initialized
//        }
//    }
//}

        binding.frequencyChipGroup.setOnCheckedStateChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId.first())
            val selectedFrequency = chip.text.toString().lowercase(Locale.ROOT)
            Log.d("checked chip ID: ", chip.toString())
            intervalFrequencyChip = selectedFrequency
            intervalFrequencyChipId = selectedChipId.first()
        }


    }

    private fun updateChip(intervalFrequencyChipId: Int, frequencyChipGroup: ChipGroup) {
        if (intervalFrequencyChipId != 0) {
            try {
                frequencyChipGroup.findViewById<Chip>(intervalFrequencyChipId).isChecked = true
            } catch (e: Exception) {

            }
        }
    }

    private fun getInterval(value: Int, frequency: String): String {
        return "$value $frequency"
    }


    private fun setIntervalValue() {
        binding.intervalNumberPicker.minValue = intervalValue
        binding.intervalNumberPicker.maxValue = DEFAULT_MAX_DAYS
        binding.intervalNumberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            intervalValue = newVal
        }
    }

    private fun splitCreateDate(): List<String> {
        val fullCreateDate = occurence.createDate
        val delim = " "
        return fullCreateDate.split(delim)
    }


    private fun getDate() {
        val datePickerFragment = DatePickerFragment()
        val supportFragmentManager = requireActivity().supportFragmentManager

        supportFragmentManager.setFragmentResultListener(
            "DATE_KEY", viewLifecycleOwner
        )
        { resultKey, bundle ->
            if (resultKey == "DATE_KEY") {
                val date = bundle.getString("SELECTED_DATE")
                binding.tvDate.text = date
            }
        }
        datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
    }

    fun getNewDateTime(date: String, time: String): String {
        return "$date $time"
    }

    private fun getTime() {
        val timePickerFragment = TimePickerFragment()
        val supportFragmentManagerTime = requireActivity().supportFragmentManager

        supportFragmentManagerTime.setFragmentResultListener(
            "TIME_KEY", viewLifecycleOwner
        )
        { resultKey, bundleTime ->
            if (resultKey == "TIME_KEY") {
                val time = bundleTime.getString("SELECTED_TIME")
                binding.tvTime.text = time
            }
        }
        timePickerFragment.show(supportFragmentManagerTime, "TimePickerFragment")
    }

    private fun addNewOccurence() {
        if (isEntryValid()) {
            val createDate =
                getNewDateTime(binding.tvDate.text.toString(), binding.tvTime.text.toString())
            viewModel.addNewOccurence(
                binding.occurenceName.text.toString(),
                createDate,
                binding.frequencySwitch.isChecked,
                binding.categoryDropdown.text.toString(),
                getInterval(intervalValue, intervalFrequencyChip)
            )
        }
        val action = NewFragmentDirections.actionNewFragmentToCounterHomeFragment()
        findNavController().navigate(action)
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.occurenceName.text.toString(),
        )
    }

    private fun updateOccurence() {
        if (isEntryValid()) {
            val createDate =
                getNewDateTime(binding.tvDate.text.toString(), binding.tvTime.text.toString())

            viewModel.updateOccurence(
                occurence.occurenceId,
                binding.occurenceName.text.toString(),
                createDate,
                binding.frequencySwitch.isChecked,
                binding.categoryDropdown.text.toString(),
                getInterval(intervalValue, intervalFrequencyChip)
            )
            val action = NewFragmentDirections.actionNewFragmentToCounterHomeFragment()
            findNavController().navigate(action)
        }
    }

    // to prevent of disappear category items from list
    override fun onResume() {
        super.onResume()
        val categories = resources.getStringArray(R.array.categories)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.category_item, categories)
        binding.categoryDropdown.setAdapter(arrayAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }


}