package com.example.counter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.adapters.DatesTimesListAdapter
import com.example.counter.data.DateTime
import com.example.counter.data.Occurence
import com.example.counter.databinding.DatesTimesItemBinding
//import androidx.navigation.fragment.navArgs
import com.example.counter.databinding.FragmentOccurenceBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class OccurenceFragment : Fragment() {

    private val navigationArgs: OccurenceFragmentArgs by navArgs()

    lateinit var occurence: Occurence
    lateinit var datesTimes: LiveData<List<DateTime>>

    private var _bindingOccurence: FragmentOccurenceBinding? = null
    private val bindingOccurence get() = _bindingOccurence!!

    private var _bindingDatesTimes: DatesTimesItemBinding? = null
    private val bindingDatesTimes get() = _bindingDatesTimes!!

    private val viewModel: CounterViewModel by viewModels {
        DateTimeViewModelFactory(
            (activity?.application as CounterApplication).database.occurenceDao(),
            (activity?.application as CounterApplication).database.dateTimeDao()
        )
    }

    private fun bind(occurence: Occurence) {
        bindingOccurence.apply {
            occurencyName.text = occurence.occurenceName
            occurencyCreateDate.text = occurence.createDate
            occurencyCategory.text = occurence.category
//            totalTimes.text = (viewModel.allOccurences as kotlin.collections.MutableList<*>).size.toString()

        }
    }

        private fun bindDatesTimes(dateTime: DateTime){
        bindingDatesTimes.apply {
            dateFull.text = dateTime.fullDate
            totalTime.text = dateTime.timeStart
            occurenceOwner.text = dateTime.occurenceOwnerId.toString()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.currentOccurence = navigationArgs.id
        viewModel.getCurrentOccurence()
        _bindingOccurence = FragmentOccurenceBinding.inflate(inflater, container, false)
        return bindingOccurence.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.id

        viewModel.retrieveOccurence(id).observe(this.viewLifecycleOwner) { selectedOccurence ->
            occurence = selectedOccurence
            bind(occurence)
        }

        datesTimes = viewModel.getCurrentOccurence()
        val adapter = DatesTimesListAdapter {
            Log.d("Occurence Fragment", "onClick z adaptera zamiast przejscia na inny fragment xD")

        }
        bindingOccurence.occurenceDetailRecyclerView.adapter = adapter
        viewModel.retrieveDatesTimes(id).observe(this.viewLifecycleOwner) { selectedOccurence ->
            selectedOccurence.let {
                adapter.submitList(it as MutableList<DateTime>?)

            }
        }
        bindingOccurence.occurenceDetailRecyclerView.layoutManager =
            LinearLayoutManager(this.context)
        bindingOccurence.startActivity.setOnClickListener { addNewDateTime() }
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

    private fun addNewDateTime() {
//    viewModel.addNewDateTime(occurence.occurenceId, getDate(),getDate(),getDate(),getDate())
        viewModel.addNewDateTime(
            navigationArgs.id,
            getDate(),
            getHour(),
            getHour(),
            "Ilestam:')"
        )  // ?????????????
    }

    private fun getDate(): String {
        val calendar = Calendar.getInstance()
        val currentDateTime = LocalDateTime.of(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND)
        )
        return currentDateTime.toString()
    }

    private fun getHour(): String {
        val calendar = Calendar.getInstance()
        val currentTime = LocalTime.of(
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
        _bindingOccurence = null
    }
}
