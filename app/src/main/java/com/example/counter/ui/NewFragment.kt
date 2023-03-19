package com.example.counter.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.counter.util.Constants.Companion.DEFAULT_HOURS
import com.example.counter.util.Constants.Companion.DEFAULT_MAX_DAYS
import com.example.counter.util.Constants.Companion.DEFAULT_MAX_HOURS
import com.example.counter.util.Constants.Companion.MINUTES
import com.example.counter.R
import com.example.counter.data.modelentity.Category
import com.example.counter.data.modelentity.CounterStatus
import com.example.counter.data.modelentity.Occurrence
import com.example.counter.databinding.FragmentNewBinding
import com.example.counter.pickers.DatePickerFragment
import com.example.counter.pickers.TimePickerFragment
import com.example.counter.util.Converter
import com.example.counter.viewmodels.CounterViewModel
import com.google.android.material.chip.Chip
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.installDisableKeyboardInput
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
    private var intervalValue = 0

    private var _binding: FragmentNewBinding? = null
    private val binding get() = _binding!!
    private lateinit var emojiPopup: EmojiPopup

    private var defaultCategories = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
    }

    private fun bind(occurrence: Occurrence) {
        binding.apply {
            emojiEditText.setText(occurrence.occurrenceIcon)
            occurrenceName.setText(occurrence.occurrenceName, TextView.BufferType.SPANNABLE)
            categoryDropdown.setText(occurrence.category.name, TextView.BufferType.SPANNABLE)
            addOccurrence.setOnClickListener { updateOccurrence() }
            tvDate.setText(splitCreateDate()[0])
            tvTime.setText(splitCreateDate()[1])
            intervalNumberPicker.minValue = DEFAULT_HOURS
            intervalNumberPicker.maxValue = DEFAULT_MAX_HOURS

            if(emojiEditText.text?.isNotEmpty() == true) emojiEditText.isCursorVisible = false

            // populate category list on edit
            val array: Array<String> = emptyArray()
            val populated = array.plus(defaultCategories.toTypedArray())
            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.category_item, populated)
            binding.categoryDropdown.setAdapter(arrayAdapter)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewBinding.inflate(inflater, container, false)

        emojiPopup = EmojiPopup(binding.root, binding.emojiEditText, onEmojiClickListener = {
            binding.emojiEditText.isCursorVisible = false
        })

        Category.values().forEach {
            defaultCategories.add(it.name)
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emojiContext = binding.emojiEditText.context
        binding.emojiEditText.apply {
            textCursorDrawable = ContextCompat.getDrawable(emojiContext, R.drawable.ic_emoji_indicator)
            installDisableKeyboardInput(emojiPopup)
            requestFocus()
        }

        val id = navigationArgs.occurenceId
        setIntervalValues()

        binding.tvDate.setOnClickListener { getDate() }
        binding.tvTime.setOnClickListener { getTime() }

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
            val selectedFrequencyChip = chip.text.toString().lowercase(Locale.ROOT)
            intervalFrequencyChip = selectedFrequencyChip
            intervalFrequencyChipId = selectedChipId.first()
        }
        //todo
        /**        fun addCategoryChip(categoryName: String) {
        val chipGroup = binding.root.findViewById<ChipGroup>(R.id.category_chipGroup)
        val newChip = Chip(chipGroup.context)
        newChip.text = categoryName
        chipGroup.addView(newChip)
        }
        // po dodaniu nowej kategorii
        val newCategoryName = "Nowa kategoria"
        addCategoryChip(newCategoryName) */




        if (id > 0) {
            viewModel.getOccurrence(id).observe(this.viewLifecycleOwner) { selectedOccurrence ->
                occurrence = selectedOccurrence
                val readFrequency = splitFrequency()
                updateChipAndValue(readFrequency)
                intervalValue = readFrequency[0].toInt()
                intervalFrequencyChip = readFrequency[1]
                bind(occurrence)
            }
        } else {
            binding.addOccurrence.setOnClickListener { addNewOccurrence() }
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

    private fun splitFrequency(): List<String> {
        val frequency = occurrence.intervalFrequency

        return frequency.split(" ")
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

    private fun addNewOccurrence() {
        val occurrenceIcon = binding.emojiEditText.text.toString()
        val occurrenceName = binding.occurrenceName.text.toString()
        val createDate =
            getNewDateTime(binding.tvDate.text.toString(), binding.tvTime.text.toString())
        val category = Category.valueOf(binding.categoryDropdown.text.toString())
        val frequency = getIntervalFrequency(intervalValue, intervalFrequencyChip)

        if (isEntryValid()) {
            viewModel.addNewOccurrence(
                occurrenceIcon,
                occurrenceName,
                createDate,
                category,
                frequency
            )
        }
        val action = NewFragmentDirections.actionNewFragmentToCounterHomeFragment()
        findNavController().navigate(action)
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.occurrenceName.text.toString(),
        )
    }

    private fun updateOccurrence() {
        if (isEntryValid()) {
            val createDate =
                getNewDateTime(binding.tvDate.text.toString(), binding.tvTime.text.toString())

            occurrence.status?.let {
                viewModel.updateOccurrence(
                    occurrence.occurrenceId,
                    occurrence.occurrenceIcon,
                    binding.occurrenceName.text.toString(),
                    createDate,
                    Category.valueOf(binding.categoryDropdown.text.toString()),
                    getIntervalFrequency(intervalValue, intervalFrequencyChip),
                    it
                )
            }
            val action = NewFragmentDirections.actionNewFragmentToCounterHomeFragment()
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()

        val array: Array<String> = emptyArray()
        val populated = array.plus(defaultCategories.toTypedArray())
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.category_item, populated)
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

data class CurrentOccurrence(
    val occurrenceId: Int = 0,
    val occurrenceName: String,
    val createDate: String,
    val category: String,
    val intervalFrequency: String,
    val status: CounterStatus? = null
)