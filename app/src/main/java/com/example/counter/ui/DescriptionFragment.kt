package com.example.counter.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.R
import com.example.counter.adapters.DescriptionsListAdapter
import com.example.counter.databinding.FragmentDescriptionBinding
import com.example.counter.viewmodels.CounterViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DescriptionFragment : Fragment() {

    private val viewModel: CounterViewModel by viewModels()

    private var _binding: FragmentDescriptionBinding? = null
    private val binding get() = _binding!!

    private val adapter: DescriptionsListAdapter by lazy { DescriptionsListAdapter() }

    private val args: DescriptionFragmentArgs by navArgs()

//    lateinit var description: Description //pozbyc sie tego
//private val description: Description by lazy { Description() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Data binding
        _binding = FragmentDescriptionBinding.inflate(inflater, container, false)

        setupRecyclerview()

        // Observe LiveData
        viewModel.getDescriptions(args.id).observe(viewLifecycleOwner) { descriptions ->
            adapter.setData(descriptions)


        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(
            requireContext(),
            "One click to copy to clipboard. Click long to delete ",
            Toast.LENGTH_SHORT
        ).show()

        binding.addBtn.setOnClickListener {
            addNewDescription()
        }


    }

    private fun setupRecyclerview() {
        val recyclerView = binding.descriptionsRecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    private fun addNewDescription() {
        viewModel.addNewDescription(
            binding.descriptionEditText.text.toString(),
            args.id
        )
    }

//    private fun deleteDescription(desc) {
//        viewModel.deleteDescription(description)
//        findNavController().navigateUp()
//    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
//                deleteDescription()
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