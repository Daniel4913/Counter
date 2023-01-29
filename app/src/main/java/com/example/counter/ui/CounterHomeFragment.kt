package com.example.counter.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.CounterApplication
import com.example.counter.adapters.OccurrenceWithDateTimeAdapter
import com.example.counter.databinding.FragmentCounterHomeBinding
import com.example.counter.viewmodels.CounterViewModel
import com.example.counter.viewmodels.DateTimeViewModelFactory
import com.google.android.material.chip.Chip

class CounterHomeFragment : Fragment() {
    private val viewModel: CounterViewModel by viewModels {
        DateTimeViewModelFactory(
            (activity?.application as CounterApplication).database.occurenceDao(),
            (activity?.application as CounterApplication).database.dateTimeDao(),
            (activity?.application as CounterApplication).database.descriptionDao()
        )
    }

    private var selectedCategoryChip = "All"
    private var selectedCategoryChipId = 0

    private var _binding: FragmentCounterHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCounterHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = OccurrenceWithDateTimeAdapter {
            val action =
                CounterHomeFragmentDirections.actionCounterHomeFragmentToOccurenceFragment(it.occurence.occurenceId, it.occurence.occurenceName)
            this.findNavController().navigate(action)
        }



        binding.occurenciesRecyclerView.adapter = adapter
        viewModel.allOccurences.observe(this.viewLifecycleOwner) { items ->
            items.let {
                it as MutableList
                adapter.submitList(it)
            }
        }
        binding.occurenciesRecyclerView.layoutManager = LinearLayoutManager(this.context)

        binding.categoryChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val chip = group.findViewById<Chip>(checkedIds.first())
            val selectedCategory = chip.text.toString()

            selectedCategoryChip = selectedCategory
            selectedCategoryChipId = checkedIds.first()

            when(selectedCategoryChip){
                "All" -> {
                    viewModel.allOccurences.observe(this.viewLifecycleOwner) { items ->
                        items.let {
                            it as MutableList
                            adapter.submitList(it)
                        }
                    }
                }
                else -> {
                    viewModel.retrieveOccurenceWithCategory(selectedCategoryChip)
                        .observe(this.viewLifecycleOwner) { items ->
                            items.let {
                                adapter.submitList(it)
                            }
                        }
                }
            }
        }



        binding.newOccurency.setOnClickListener {
            val action = CounterHomeFragmentDirections.actionCounterHomeFragmentToNewFragment("Create new occurence")
            this.findNavController().navigate(action)
        }
    }
}