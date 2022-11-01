package com.example.counter

//import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.counter.data.Occurence
//import androidx.navigation.fragment.navArgs
import com.example.counter.databinding.FragmentNewBinding

//import kotlin.reflect.KProperty


class NewFragment : Fragment() {

//    private val navigationArgs: OccurenceFragmentArgs by navArgs()

    private val viewModel: CounterViewModel by viewModels {
        DateTimeViewModelFactory(
            (activity?.application as CounterApplication).database.occurenceDao(),
            (activity?.application as CounterApplication).database.dateTimeDao()
        )
    }

    lateinit var occurence: Occurence

    private var _binding: FragmentNewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addBtn.setOnClickListener {
            addNewOccurence()
        }
        binding.currentDateTime.text = "pobierz date"
        binding.currentDateTime.setOnClickListener {
            binding.occurenceDate.setText(getDate(), TextView.BufferType.EDITABLE)
        }
    }

    private fun addNewOccurence() {
        if (isEntryValid()) {
//            Pass in the item details entered by the user, use the binding instance to read them.
            viewModel.addNewOccurence(
                binding.occurenceName.text.toString(),
                binding.occurenceDate.text.toString(),
                binding.frequencySwitch.isChecked,
                binding.categoryList.selectedPosition.toString() //????
            )
        }
        val action = NewFragmentDirections.actionNewFragmentToCounterHomeFragment()
        findNavController().navigate(action)
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.occurenceName.text.toString(),
            binding.occurenceDate.text.toString(),
            binding.category.text.toString()
        )
    }


    private fun getDate(): String {
        return viewModel.getDate()
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