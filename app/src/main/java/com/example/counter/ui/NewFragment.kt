package com.example.counter.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.counter.util.Constants.Companion.DEFAULT_HOURS
import com.example.counter.util.Constants.Companion.DEFAULT_MAX_DAYS
import com.example.counter.util.Constants.Companion.DEFAULT_MAX_HOURS
import com.example.counter.util.Constants.Companion.MINUTES
import com.example.counter.R
import com.example.counter.data.modelentity.Occurrence
import com.example.counter.databinding.FragmentNewBinding
import com.example.counter.pickers.DatePickerFragment
import com.example.counter.pickers.TimePickerFragment
import com.example.counter.viewmodels.CounterViewModel
import com.google.android.material.chip.Chip
import com.vanniktech.emoji.installForceSingleEmoji
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NewFragment : Fragment() {
    private val navigationArgs: NewFragmentArgs by navArgs()

    private lateinit var viewModel: CounterViewModel
    private lateinit var occurrence: Occurrence

    private var intervalFrequencyChip = MINUTES
    private var intervalFrequencyChipId = 0
    private var intervalValue = DEFAULT_HOURS

//    lateinit var emojiProvider: EmojiProvider

    private var _binding: FragmentNewBinding? = null
    private val binding get() = _binding!!
    private var emojiIcon = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
    }

    private fun bind(occurrence: Occurrence) {
        binding.apply {
            occurenceName.setText(occurrence.occurrenceName, TextView.BufferType.SPANNABLE)
            categoryDropdown.setText(occurrence.category, TextView.BufferType.SPANNABLE)
            addBtn.setOnClickListener { updateOccurrence() }
            tvDate.setText(splitCreateDate()[0])
            tvTime.setText(splitCreateDate()[1])
            intervalNumberPicker.minValue = DEFAULT_HOURS
            intervalNumberPicker.maxValue = DEFAULT_MAX_HOURS
            emojiEditText.setText(emojiIcon)

            //populate category list on edit
            //TODO ALLOW USER TO CREATE OWN CATEGORY (NEED TO IMPLEMENT DATA STORE FIRST)
            val categories: Array<String> = resources.getStringArray(R.array.categories)
            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.category_item, categories)
            binding.categoryDropdown.setAdapter(arrayAdapter)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewBinding.inflate(inflater, container, false)


//        val emojiManager = EmojiManager.install(emojiProvider)
//        val emojiPopup = EmojiPopup(binding.root, binding.emojiEditText)
//        binding.iconInfinity.setOnClickListener { emojiPopup.show() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.occurenceId


        setIntervalValues()

        binding.tvDate.setOnClickListener { getDate() }
        binding.tvTime.setOnClickListener { getTime() }
        binding.frequencySwitch.setOnClickListener { updateFrequencySwitchText() }

        Toast.makeText(requireContext(), "Click on date and time to change it", Toast.LENGTH_LONG)
            .show()

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val now = "${hourOfDay}:${minute}"

        if (binding.tvTime.text !== now) {
            binding.tvTime.text = getString(R.string.colon_time, hourOfDay, minute)
            binding.tvDate.text = getString(R.string.minus_date, day, month, year)
        }

        binding.frequencyChipGroup.setOnCheckedStateChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId.first())
            val selectedFrequency = chip.text.toString().lowercase(Locale.ROOT)
            intervalFrequencyChip = selectedFrequency
            intervalFrequencyChipId = selectedChipId.first()
        }

        if (id > 0) {
            viewModel.getOccurrence(id).observe(this.viewLifecycleOwner) { selectedOccurrence ->
                occurrence = selectedOccurrence

                val readFrequence = splitFrequence()
                updateChipAndValue(readFrequence)
                intervalValue = readFrequence[0].toInt()
                intervalFrequencyChip = readFrequence[1]

                bind(occurrence)
            }
        } else {
            binding.addBtn.setOnClickListener { addNewOccurrence() }
        }
    }

    private fun updateChipAndValue(intervalValues: List<String>) {
        val pickerValue = intervalValues[0].toInt()
        val chipValue = intervalValues[1]



        val value: Int = when (chipValue) {
            "minutes" -> R.id.chip_minutes
            "hours" -> R.id.chip_hours
            "days" -> R.id.chip_days
            "weeks" -> R.id.chip_weeks
            else -> {
                R.id.chip_months
            }
        }

        binding.intervalNumberPicker.value = pickerValue
        binding.frequencyChipGroup.check(value)
    }

    private fun updateFrequencySwitchText() {
        val switch = binding.frequencySwitch
        when (switch.isChecked) {
            true -> {
                switch.text = getText(R.string.more_often)
            }
            false -> {
                switch.text = getText(R.string.less)
            }
        }
    }


    private fun getIntervalFrequency(value: Int, frequency: String): String {
        return "$value $frequency"
    }


    private fun setIntervalValues() {
        binding.intervalNumberPicker.minValue = intervalValue
        binding.intervalNumberPicker.maxValue = DEFAULT_MAX_DAYS
        binding.intervalNumberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            intervalValue = newVal
        }

    }

    private fun splitCreateDate(): List<String> {
        val createDate = occurrence.createDate
        return createDate.split(" ")
    }

    private fun splitFrequence(): List<String> {
        val frequence = occurrence.intervalFrequency
        return frequence.split(" ")

    }

    private fun getNewDateTime(date: String, time: String): String {
        return "$date $time"
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

    private fun occIconAndName(): String {
        val icon = binding.emojiEditText.text
        val name = binding.occurenceName.text
        binding.emojiEditText.installForceSingleEmoji()
        binding.emojiEditText.isEmojiCompatEnabled

        return "$icon $name"
    }

    private fun addNewOccurrence() {
        if (isEntryValid()) {
            val createDate =
                getNewDateTime(binding.tvDate.text.toString(), binding.tvTime.text.toString())
            viewModel.addNewOccurence(
                occIconAndName(),
                createDate,
                binding.frequencySwitch.isChecked,
                binding.categoryDropdown.text.toString(),
                getIntervalFrequency(intervalValue, intervalFrequencyChip)
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

    private fun updateOccurrence() {
        if (isEntryValid()) {
            val createDate =
                getNewDateTime(binding.tvDate.text.toString(), binding.tvTime.text.toString())

            viewModel.updateOccurence(
                occurrence.occurrenceId,
                binding.occurenceName.text.toString(),
                createDate,
                binding.frequencySwitch.isChecked,
                binding.categoryDropdown.text.toString(),
                getIntervalFrequency(intervalValue, intervalFrequencyChip)
            )
            val action = NewFragmentDirections.actionNewFragmentToCounterHomeFragment()
            findNavController().navigate(action)
        }
    }

    //     to prevent of disappear category items from list
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