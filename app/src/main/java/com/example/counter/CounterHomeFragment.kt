package com.example.counter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.counter.databinding.FragmentCounterHomeBinding

class CounterHomeFragment : Fragment() {
    private val viewModel: CounterViewModel by viewModels {
        DateTimeViewModelFactory(
            (activity?.application as CounterApplication).database.occurenceDao(),
            (activity?.application as CounterApplication).database.dateTimeDao()
        )
    }

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
        val adapter = OccurenceListAdapter {
            val action =
                CounterHomeFragmentDirections.actionCounterHomeFragmentToOccurenceFragment(it.occurenceId)
            this.findNavController().navigate(action)
        }
        binding.occurenciesRecyclerView.adapter = adapter
        viewModel.allOccurences.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }

        binding.occurenciesRecyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.newOccurency.setOnClickListener {
            val action = CounterHomeFragmentDirections.actionCounterHomeFragmentToNewFragment()
            this.findNavController().navigate(action)
        }
    }
}