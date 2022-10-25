package com.example.counter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.adapters.DatesTimesListAdapter
import com.example.counter.data.DateTime
import com.example.counter.data.Occurence
//import androidx.navigation.fragment.navArgs
import com.example.counter.databinding.FragmentOccurenceBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDateTime
import java.util.*

class OccurenceFragment : Fragment() {

    private val navigationArgs: OccurenceFragmentArgs by navArgs()


    private val viewModel: CounterViewModel by viewModels {
        DateTimeViewModelFactory(
            (activity?.application as CounterApplication).database.occurenceDao(),
            (activity?.application as CounterApplication).database.dateTimeDao()
        )
    }

    private fun bind(occurence: Occurence){
        binding.apply {
            occurencyName.text = occurence.occurenceName
            occurencyCreateDate.text = occurence.createDate
            occurencyCategory.text = occurence.category
        }
    }

    private fun bindDatesTimes(dateTime: DateTime){
        binding.apply {

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

        // do datetime:
        val adapter = DatesTimesListAdapter{
            Log.d("Occurence Fragment","onClick z adaptera zamiast przejscia na inny fragment xD")
        }
        binding.occurenceDetailRecyclerView.adapter = adapter

        viewModel.allDatesTimes.observe(this.viewLifecycleOwner){ items ->
            items.let{
                adapter.submitList(it)
            }
        }
        binding.occurenceDetailRecyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.startActivity.setOnClickListener { addNewDateTime() }
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

private fun addNewDateTime(){
//    viewModel.addNewDateTime(occurence.occurenceId, getDate(),getDate(),getDate(),getDate())
    viewModel.addNewDateTime(navigationArgs.id, getDate(),getDate(),getDate(),getDate())  // ?????????????
}

private fun getDate(): String{
    val calendar = Calendar.getInstance()
    val currentTime = LocalDateTime.of(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        calendar.get(Calendar.SECOND)
    )
    return currentTime.toString()
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
