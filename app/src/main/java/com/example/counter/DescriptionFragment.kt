package com.example.counter

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.adapters.DescriptionListAdapter
import com.example.counter.data.Description
import com.example.counter.databinding.FragmentDescriptionBinding
import com.example.counter.viewmodels.CounterViewModel
import com.example.counter.viewmodels.DateTimeViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DescriptionFragment : Fragment() {

    private val navigationArgs: DescriptionFragmentArgs by navArgs()

    lateinit var description: Description

    private var _binding: FragmentDescriptionBinding? = null
    private val binding get() = _binding!!


    private val viewModel: CounterViewModel by viewModels {
        DateTimeViewModelFactory(
            (activity?.application as CounterApplication).database.occurenceDao(),
            (activity?.application as CounterApplication).database.dateTimeDao(),
            (activity?.application as CounterApplication).database.descriptionDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun addNewDescription() {
        viewModel.addNewDescription(
            binding.descriptionEditText.text.toString(),
            navigationArgs.id
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val duration = Toast.LENGTH_LONG
        val text = "One click to copy to clipboard. Click long to delete "
        val toast = Toast.makeText(requireContext(), text, duration)
        toast.show()

        binding.addBtn.setOnClickListener {
            addNewDescription()
        }

        val adapter = DescriptionListAdapter {
            description = it
            showConfirmationDialog()
        }

        binding.descriptionsRecyclerView.adapter = adapter

        val id = navigationArgs.id
        viewModel.retrieveDescriptions(id).observe(this.viewLifecycleOwner) { selectedOcurence ->
            selectedOcurence.let {
                adapter.submitList(it)
            }
        }
        binding.descriptionsRecyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    private fun deleteDescription() {
        viewModel.deleteDescription(description)
        findNavController().navigateUp()
    }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteDescription()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}