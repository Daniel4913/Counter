package com.example.counter

//import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.counter.data.Occurence
//import androidx.navigation.fragment.navArgs
import com.example.counter.databinding.FragmentNewBinding

//import kotlin.reflect.KProperty


class NewFragment : Fragment() {

//    private val navigationArgs: OccurenceFragmentArgs by navArgs()

    private val viewModel: CounterViewModel by activityViewModels {
        CounterViewModelFactory(
            (activity?.application as CounterApplication).database
                .occurenceDao()
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

    private fun addNewOccurence() {
        if (isEntryValid()) {
//            Pass in the item details entered by the user, use the binding instance to read them.
            viewModel.addNewOccurence(
                binding.occurenceName.text.toString(),
                binding.occurenceDate.text.toString(),
                binding.frequencySwitch.isChecked,
                binding.category.text.toString()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addBtn.setOnClickListener {
            addNewOccurence()
        }
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
