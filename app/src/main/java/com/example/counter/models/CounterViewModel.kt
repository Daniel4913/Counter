package com.example.counter

import androidx.lifecycle.*
import com.example.counter.data.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CounterViewModel(
    private val occurenceDao: OccurenceDao,
    private val dateTimeDao: DateTimeDao,
    private val descriptionDao: DescriptionDao
) : ViewModel() {

    val allOccurences: LiveData<List<Occurence>> = occurenceDao.getOccurencies().asLiveData()
    var currentOccurence = 0

    fun getOccurenceDatesTimes(): LiveData<List<DateTime>> {
        lateinit var allDatesTimes: LiveData<List<DateTime>>
        if (currentOccurence != 0) {
            var gettedOccurence = 0
            var getAllDatesTimes: LiveData<List<DateTime>> =
                dateTimeDao.getOccurenceWithDatesTimes(gettedOccurence).asLiveData()
            allDatesTimes = getAllDatesTimes
        }
        return allDatesTimes
    }

    fun getOccurenceDescriptions(): LiveData<List<Description>> {
        lateinit var allDescriptions: LiveData<List<Description>>
        if (currentOccurence != -1) {
            var gettedOccurence = 0
//            var getDescriptions = descriptionDao.getOccurenceWithDescriptions(gettedOccurence).asLiveData()
            var getDescriptions = retrieveDescriptions(gettedOccurence)
            allDescriptions = getDescriptions
        }
        return allDescriptions
    }

    fun retrieveOccurence(id: Int): LiveData<Occurence> {
        return occurenceDao.getOccurence(id).asLiveData()
    }

    fun retrieveDatesTimes(id: Int): LiveData<List<DateTime>> {
        return dateTimeDao.getOccurenceWithDatesTimes(id).asLiveData()
    }

    fun retrieveDescriptions(id: Int): LiveData<List<Description>> {
        return descriptionDao.getOccurenceWithDescriptions(id).asLiveData()
    }

    //    To interact with the database off the main thread, start a coroutine and call the DAO method within it
    private fun insertOccurence(occurence: Occurence) {
        viewModelScope.launch {
            occurenceDao.insertOccurence(occurence)
        }
    }

    fun deleteOccurence(occurence: Occurence) {
        viewModelScope.launch {
            occurenceDao.delete(occurence)
        }
    }

    fun updateOccurence(occurence: Occurence) {
        viewModelScope.launch {
            occurenceDao.update(occurence)
        }
    }

    //  function that takes in strings and boolean and returns an Occurence instance.
    private fun getNewOccurenceEntry(
        occurenceName: String,
        createDate: String,
        occurMore: Boolean,
        category: String,
        intervalValue: Int,
        intervalFrequency: String
    ): Occurence {
        return Occurence(
            occurenceName = occurenceName,
            createDate = createDate,
            occurMore = occurMore,
            category = category,
            intervalValue = intervalValue,
            intervalFrequency = intervalFrequency
        )
    }

    // fn to acquire data from newfragment
    fun addNewOccurence(
        occurenceName: String,
        createDate: String,
        occurMore: Boolean,
        category: String,
        intervalValue: Int,
        intervalFrequency: String
    ) {
        val newOccurence = getNewOccurenceEntry(occurenceName, createDate, occurMore, category, intervalValue,intervalFrequency)
        insertOccurence(newOccurence)
    }

    fun updateOccurence(
        occurenceId: Int,
        occurenceName: String,
        createDate: String,
        occurMore: Boolean,
        category: String,
        interavalValue: Int,
        intervalFrequency: String
    ) {
        val updatedOccurence = getUpdatedOccurenceEntry(
            occurenceId = occurenceId,
            occurenceName = occurenceName,
            createDate = createDate,
            occurMore = occurMore,
            category = category,
            intervalValue = interavalValue,
            intervalFrequency = intervalFrequency
        )
        updateOccurence(updatedOccurence)
    }

    private fun getUpdatedOccurenceEntry(
        occurenceId: Int,
        occurenceName: String,
        createDate: String,
        occurMore: Boolean,
        category: String,
        intervalValue: Int,
        intervalFrequency: String
    ): Occurence {
        return Occurence(
            occurenceId = occurenceId,
            occurenceName = occurenceName,
            createDate = createDate,
            occurMore = occurMore,
            category = category,
            intervalValue = intervalValue,
            intervalFrequency = intervalFrequency
        )
    }

    fun isEntryValid(occurenceName: String): Boolean {
        if (occurenceName.isBlank()) {
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

    fun deleteDateTime(dateTime: DateTime) {
        viewModelScope.launch {
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

    fun getHour(): String {
        val currentTime = LocalDateTime.now()
        return currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    }

    /**
     * Description block
     */

    private fun insertDescription(description: Description) {
        viewModelScope.launch {
            descriptionDao.insertDescription(description)
        }
    }

    fun deleteDescription(description: Description) {
        viewModelScope.launch {
            descriptionDao.deleteDescription(description)
        }
    }

    private fun getNewDescriptionEntry(
        descriptionNote: String,
        occurenceOwnerId: Int
    ): Description {
        return Description(
            descriptionDate = LocalDateTime.now().toString(),
            descriptionNote = descriptionNote,
            occurenceOwnerId = occurenceOwnerId
        )
    }

    fun addNewDescription(
        descriptionNote: String,
        occurenceOwnerId: Int
    ) {
        val newDescription = getNewDescriptionEntry(descriptionNote, occurenceOwnerId)
        insertDescription(newDescription)
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

class DateTimeViewModelFactory(
    private val occurenceDao: OccurenceDao,
    private val dateTimeDao: DateTimeDao,
    private val descriptionDao: DescriptionDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CounterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CounterViewModel(occurenceDao, dateTimeDao, descriptionDao) as T
        }
        throw IllegalArgumentException("Unknow view model classs////////////")
    }
}