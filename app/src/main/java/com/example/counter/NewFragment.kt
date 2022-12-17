package com.example.counter


import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog.show
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.counter.data.Occurence
import com.example.counter.databinding.FragmentNewBinding
import com.example.counter.pickers.DatePickerFragment


class NewFragment : Fragment(){

    val navigationArgs: NewFragmentArgs by navArgs()

    private val viewModel: CounterViewModel by viewModels {
        DateTimeViewModelFactory(
            (activity?.application as CounterApplication).database.occurenceDao(),
            (activity?.application as CounterApplication).database.dateTimeDao(),
            (activity?.application as CounterApplication).database.descriptionDao()
        )
    }

    lateinit var occurence: Occurence

    private var _binding: FragmentNewBinding? = null
    private val binding get() = _binding!!

    private fun bind(occurence: Occurence) {
        binding.apply {
            occurenceName.setText(occurence.occurenceName, TextView.BufferType.SPANNABLE)
            categoryDropdown.setText(occurence.category, TextView.BufferType.SPANNABLE)
//            tvDate.setText(datePickerDialog().toString(), TextView.BufferType.SPANNABLE)
            addBtn.setOnClickListener { updateOccurence() }
        }
    }

    //in onResume to prevent of disappear category items from list
    override fun onResume() {
        super.onResume()
        val categories = resources.getStringArray(R.array.categories)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.category_item, categories)
        binding.categoryDropdown.setAdapter(arrayAdapter)
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
            binding.addBtn.setOnClickListener {
                addNewOccurence()
            }
        }
        binding.tvDate.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            val supportFragmentManager = requireActivity().supportFragmentManager

            supportFragmentManager.setFragmentResultListener(
                "REQUEST_KEY",viewLifecycleOwner)
            { resultKey, bundle ->
                    if (resultKey == "REQUEST_KEY"){
                        val date = bundle.getString("SELECTED_DATE")
                        binding.tvDate.text = date
            Log.d("NewFragment","//////////////tvDate binding dadte text")
                }
            }
            datePickerFragment.show(supportFragmentManager,"DatePickerFragment") // wyświetla się co prawda ale wciąż nie wiem jak pobrać tą datę :))
        }
    }

    private fun addNewOccurence() {
        if (isEntryValid()) {
//            Pass in the item details entered by the user, use the binding instance to read them.
            viewModel.addNewOccurence(
                binding.occurenceName.text.toString(),
                getDate(),
                binding.frequencySwitch.isChecked,
                binding.categoryDropdown.text.toString()
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
            viewModel.updateOccurence(
                this.navigationArgs.occurenceId,
                this.binding.occurenceName.text.toString(),
                occurence.createDate, //pobiera pierwotną date stworzenia occurence
                this.binding.frequencySwitch.isChecked,
                this.binding.categoryDropdown.text.toString()
            )
            val action = NewFragmentDirections.actionNewFragmentToCounterHomeFragment()
            findNavController().navigate(action)
        }
    }

    fun getDate(): String{
        return ""
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