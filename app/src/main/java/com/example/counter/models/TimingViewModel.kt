package com.example.counter.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.counter.data.OccurenceDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimingViewModel(private val occurenceDao: OccurenceDao): ViewModel() {

    val readDateAndInterval = occurenceDao.getOccurence(0)

    fun saveDateAndInterval(occurenceId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            occurenceDao.getOccurence(occurenceId)
        }
    }
}