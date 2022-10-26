package com.example.counter

import androidx.lifecycle.*
import com.example.counter.data.DateTime
import com.example.counter.data.DateTimeDao
import com.example.counter.data.Occurence
import com.example.counter.data.OccurenceDao
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class CounterViewModel(private val occurenceDao: OccurenceDao, private val dateTimeDao: DateTimeDao)
    : ViewModel() {


    val allOccurences: LiveData<List<Occurence>> = occurenceDao.getOccurencies().asLiveData()

     var currentOccurence = 0

    fun getCurrentOccurence(): LiveData<List<DateTime>> {
        lateinit var allDatesTimes: LiveData<List<DateTime>>
        if (currentOccurence!=0){
            var gettedOccurence = 0
            var getAllDatesTimes: LiveData<List<DateTime>> = dateTimeDao.getOccurenceWithDatesTimes(gettedOccurence).asLiveData()
            allDatesTimes = getAllDatesTimes
        }
        return allDatesTimes
    }

    fun retrieveOccurence(id: Int): LiveData<Occurence>{
        return occurenceDao.getOccurence(id).asLiveData()
    }

    fun retrieveDatesTimes(id: Int): LiveData<List<DateTime>>{
        return dateTimeDao.getOccurenceWithDatesTimes(id).asLiveData()
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

    fun getDate(): String {
        val calendar = Calendar.getInstance()
        val currentDate = LocalDate.of(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        )
        return currentDate.toString()
    }

    fun getHour(): String{
        val calendar = Calendar.getInstance()
        val currentTime = LocalTime.of(
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND)
        )
        return currentTime.toString()
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