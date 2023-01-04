package com.example.counter.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.counter.data.Occurence
import com.example.counter.data.OccurenceDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimingViewModel(private val occurenceDao: OccurenceDao): ViewModel() {


    fun readInterval(currentOccurence: Int){

    }

    

}