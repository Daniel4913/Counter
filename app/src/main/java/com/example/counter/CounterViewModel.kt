package com.example.counter

import androidx.lifecycle.*
import com.example.counter.data.DateTime
import com.example.counter.data.DateTimeDao
import com.example.counter.data.Occurence
import com.example.counter.data.OccurenceDao
import kotlinx.coroutines.launch

class CounterViewModel(private val occurenceDao: OccurenceDao, private val dateTimeDao: DateTimeDao) : ViewModel() {

//    val currentVisitedOccurence = !!!!!!!!!!!!!!!!!!!

    val allOccurences: LiveData<List<Occurence>> = occurenceDao.getOccurencies().asLiveData()
    val allDatesTimes: LiveData<List<DateTime>> = dateTimeDao.getOccurenceWithDatesTimes().asLiveData()
// skad sie dowiedziec ktore occurency klilknal user??????????????????????????????????????   ^
    //ta informacja jest w home fragment

    fun retrieveOccurence(id: Int): LiveData<Occurence>{
        return occurenceDao.getOccurence(id).asLiveData()
    }


//    To interact with the database off the main thread, start a coroutine and call the DAO method within it
    private fun insertOccurence(occurence: Occurence) {
        viewModelScope.launch {
            occurenceDao.insertOccurence(occurence) }
    }

//  function that takes in strings and boolean and returns an Occurence instance.
    private fun getNewOccurenceEntry(
        occurenceName: String,
        occurenceDate: String,
        occurMore: Boolean,
        category: String
    ): Occurence {
        return Occurence(
            occurenceName = occurenceName,
            createDate = occurenceDate,
            occurMore = occurMore,
//            occurMore = occurMore.toString(), = drze pape ze type mismatch, chyba pojebalem strony co do czego XD
            category = category
//        occurenceName = name, = Cannot find a parameter with this name: occurenceName . Czyli dobre strony jednak
        )
    }

    // fn to acquire data from fragment
    fun addNewOccurence(
        occurenceName: String,
        occurenceDate: String,
        occurMore: Boolean,
        category: String
    ) {
        val newOccurence = getNewOccurenceEntry(occurenceName, occurenceDate, occurMore, category)
        insertOccurence(newOccurence)
    }
    fun isEntryValid(occurenceName: String, occurenceDate: String, category: String): Boolean {
        if (occurenceName.isBlank() || occurenceDate.isBlank() || category.isBlank()) {
            return false
        }
        return true
    }

    private fun insertDateTime(dateTime: DateTime) {
        viewModelScope.launch {
            dateTimeDao.insertDateTime(dateTime)
        }
    }
    private fun getNewDateTimeEntry(
        occurenceOwnerId: Int,
        fullDate: String,
        timeStart: String,
        timeStop: String,
        totalTime: String,

        ): DateTime {
        return DateTime(
            occurenceOwnerId = occurenceOwnerId,
            fullDate = fullDate,
            timeStart = timeStart,
            timeStop = timeStop,
            totalTime = totalTime
        )
    }

    fun addNewDateTime(
        occurenceOwnerId: Int,
        fullDate: String,
        timeStart: String,
        timeStop: String,
        totalTime: String,
    ) {
        val newDateTime = getNewDateTimeEntry(
            occurenceOwnerId, fullDate, timeStart,
            timeStop,
            totalTime
        )
        insertDateTime(newDateTime)
    }
}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */

class DateTimeViewModelFactory(private val occurenceDao: OccurenceDao, private val dateTimeDao: DateTimeDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CounterViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return CounterViewModel(occurenceDao, dateTimeDao) as T
        }
        throw IllegalArgumentException("Unknow view model classs////////////")
    }
}