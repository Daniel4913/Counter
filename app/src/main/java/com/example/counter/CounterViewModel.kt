package com.example.counter

import androidx.lifecycle.*
import com.example.counter.data.DateTime
import com.example.counter.data.DateTimeDao
import com.example.counter.data.Occurence
import com.example.counter.data.OccurenceDao
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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

    fun deleteOccurence(occurence: Occurence){
        viewModelScope.launch {
            occurenceDao.delete(occurence)
        }
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
            category = category
        )
    }

    // fn to acquire data from newfragment
    fun addNewOccurence(
        occurenceName: String,
        occurenceDate: String,
        occurMore: Boolean,
        category: String
    ) {
        val newOccurence = getNewOccurenceEntry(occurenceName, occurenceDate, occurMore, category)
        insertOccurence(newOccurence)
    }
    fun isEntryValid(occurenceName: String, occurenceDate: String): Boolean {
        if (occurenceName.isBlank() || occurenceDate.isBlank()) {
            return false
        }
        return true
    }

// DATES TIMES BLOCK
    private fun insertDateTime(dateTime: DateTime) {
        viewModelScope.launch {
            dateTimeDao.insertDateTime(dateTime)
        }
    }

    fun deleteDateTime(dateTime: DateTime){
        viewModelScope.launch{
            dateTimeDao.delete(dateTime)
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
        val currentDateTime = LocalDateTime.now()
        return currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"))
    }

    fun getHour(): String{
        val currentTime = LocalDateTime.now()
        return currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    }

    /**
     * Counting bloc, to calculate how much time passed between occurences
     */




        ////CHRONO UTNIT
//        val date1 = LocalDateTime.now()
//        val date2 = LocalDateTime.of(2022,Month.OCTOBER,30,11,0,0)
//        val date3 = LocalDateTime.of(2022,Month.OCTOBER,30,11,0,0)
//        val pattern = "HH:mm:ss dd-MM-yyyy"
//        val formatter = DateTimeFormatter.ofPattern(pattern)

//        val date4 = LocalDateTime.parse(lastDateTime, formatter)

//        println("ChronoUnit.DAYS.between(date1, date2) ${ChronoUnit.DAYS.between(date3, date1)}")
//        println("ChronoUnit.DAYS.between(date1, date2) ${ChronoUnit.HOURS.between(date4, date1)}")
//        println("Duration.between(date1, date2).toDays() ${Duration.between(date1, date2).toDays()}")
//        println("date1.until(date2, ChronoUnit.DAYS) ${date1.until(date2, ChronoUnit.DAYS)}")
//        println()
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