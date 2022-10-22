package com.example.counter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.counter.data.Occurence
//import androidx.navigation.fragment.navArgs
import com.example.counter.databinding.FragmentOccurenceBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class OccurenceFragment : Fragment() {

    private val navigationArgs: OccurenceFragmentArgs by navArgs()

    private val viewModel: CounterViewModel by activityViewModels {
        CounterViewModelFactory(
            (activity?.application as CounterApplication).database.occurenceDao()
        )
    }

    private fun bind(occurence: Occurence){
        binding.apply {
            occurencyName.text = occurence.occurenceName
            occurencyCreateDate.text = occurence.createDate
            occurencyCategory.text = occurence.category
        }

    }

    lateinit var occurence: Occurence

    private var _binding: FragmentOccurenceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentOccurenceBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.id
        viewModel.retrieveOccurence(id).observe(this.viewLifecycleOwner){ selectedOccurence ->
            occurence = selectedOccurence
            bind(occurence)
        }
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
                deleteItem()
            }
            .show()
    }

    /**
     * Deletes the current item and navigates to the list fragment.
     */
    private fun deleteItem() {
        findNavController().navigateUp()
    }

    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}