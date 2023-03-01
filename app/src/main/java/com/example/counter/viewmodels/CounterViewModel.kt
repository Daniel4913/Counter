package com.example.counter.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.counter.data.*
import com.example.counter.data.DataStoreRepository
import com.example.counter.data.DataStoreRepository.FilterCategory
import com.example.counter.data.modelentity.Activity
import com.example.counter.data.modelentity.Description
import com.example.counter.data.modelentity.Occurrence
import com.example.counter.data.relations.OccurrenceWithActivities
import com.example.counter.data.relations.OccurrenceWithDescripion
import com.example.counter.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    private lateinit var filterCategory: FilterCategory

//    val readFilterCategory = dataStoreRepository.readFilterCategory


    private fun saveFilterCategory(){
        viewModelScope.launch(Dispatchers.IO) {
            if(this@CounterViewModel::filterCategory.isInitialized){
                dataStoreRepository.saveFilterCategory(
                    filterCategory.filteredCategoryChip,
                    filterCategory.filteredCategoryChipId
                )
            }
        }
    }

    fun saveFilterCategoryTemp(
        filterCategoryChip: String,
        filterCategoryChipId: Int
    ){
        filterCategory = FilterCategory(
            filterCategoryChip,
            filterCategoryChipId
        )
        saveFilterCategory()
    }


    val readOccurrencesWithActivities: LiveData<List<OccurrenceWithActivities>> =
        repository.dataSource.getOccurrencesWithActivities().asLiveData()

    fun getOccurrence(id: Int): LiveData<Occurrence> {
        return repository.dataSource.getOccurrence(id).asLiveData()
    }

    fun getOccurrencesByCategory(category: String): LiveData<List<OccurrenceWithActivities>> {
        return repository.dataSource.getOccurrencesByCategory(category).asLiveData()
    }

    fun getOccurrenceWithActivities(): LiveData<List<OccurrenceWithActivities>> {
        return repository.dataSource.getOccurrencesWithActivities().asLiveData()
    }

    fun getActivities(id: Int): LiveData<List<Activity>> {
        return repository.dataSource.getOccurrenceActivities(id).asLiveData()
    }

    fun getAllActivities(): LiveData<List<Activity>> {
        return repository.dataSource.getAllActivities().asLiveData()
    }

    fun updateSeconds(activityId: Int, secondsFrom: Long, secondsTo: Long) {
        viewModelScope.launch {
            repository.dataSource.updateSeconds(activityId, secondsFrom, secondsTo)
        }
    }

    fun updateActivity(activity: Activity) {
        viewModelScope.launch { repository.dataSource.updateActivity(activity) }
    }

    fun retrieveOccurrenceWithDescriptions(): LiveData<List<OccurrenceWithDescripion>> {
        return repository.dataSource.getOccurrenceWithDescriptions().asLiveData()
    }

    fun getDescriptions(id: Int): LiveData<List<Description>> {
        return repository.dataSource.getDescriptions(id).asLiveData()
    }

    private fun insertOccurence(occurrence: Occurrence) {
        viewModelScope.launch {
            repository.dataSource.insertOccurrence(occurrence)
        }
    }

    fun deleteOccurence(occurrence: Occurrence) {
        viewModelScope.launch {
            repository.dataSource.deleteOccurrence(occurrence)
        }
    }

    fun updateOccurrence(occurrence: Occurrence) {
        viewModelScope.launch {
            repository.dataSource.updateOccurrence(occurrence)
        }
    }

//    fun updateDateTime(dateTime: OccurrenceWithDatesTimes){
//        viewModelScope.launch {
//            occurenceDao.updateOccurrenceWithDateTime(dateTime)
//        }
//    }


    private fun getNewOccurrenceEntry(
        occurrenceName: String,
        createDate: String,
        occurMore: Boolean,
        category: String,
        intervalFrequency: String
    ): Occurrence {
        return Occurrence(
            occurrenceName = occurrenceName,
            createDate = createDate,
            occurMore = occurMore,
            category = category,
            intervalFrequency = intervalFrequency
        )
    }

    fun addNewOccurrence(
        occurrenceName: String,
        createDate: String,
        occurMore: Boolean,
        category: String,
        intervalFrequency: String
    ) {
        val newOccurrence =
            getNewOccurrenceEntry(occurrenceName, createDate, occurMore, category, intervalFrequency)
        insertOccurence(newOccurrence)
    }

    fun updateOccurrence(
        occurrenceId: Int,
        occurrenceName: String,
        createDate: String,
        occurMore: Boolean,
        category: String,
        intervalFrequency: String
    ) {
        val updatedOccurence = getUpdatedOccurrenceEntry(
            occurrenceId = occurrenceId,
            occurrenceName = occurrenceName,
            createDate = createDate,
            occurMore = occurMore,
            category = category,
            intervalFrequency = intervalFrequency
        )
        updateOccurrence(updatedOccurence)
    }

    private fun getUpdatedOccurrenceEntry(
        occurrenceId: Int,
        occurrenceName: String,
        createDate: String,
        occurMore: Boolean,
        category: String,
        intervalFrequency: String
    ): Occurrence {
        return Occurrence(
            occurrenceId = occurrenceId,
            occurrenceName = occurrenceName,
            createDate = createDate,
            occurMore = occurMore,
            category = category,
            intervalFrequency = intervalFrequency
        )
    }

    fun isEntryValid(occurrenceName: String): Boolean {
        if (occurrenceName.isBlank()) {
            return false
        }
        return true
    }

    // DATES TIMES BLOCK
    private fun insertActivity(activity: Activity) {
        viewModelScope.launch {
            repository.dataSource.insertActivity(activity)
        }
    }

    fun deleteActivity(activity: Activity) {
        viewModelScope.launch {
            repository.dataSource.deleteActivity(activity)
        }
    }

    fun getActivity(id: Int): LiveData<Activity> {
        return repository.dataSource.getActivity(id).asLiveData()
    }

    private fun getNewActivity(
        occurenceOwnerId: Int,
        fullDate: String,
        timeStart: String,
        secondsFromLast: Long,
        intervalSeconds: Long,
        secondsToNext: Long,

        ): Activity {
        return Activity(
            occurrenceOwnerId = occurenceOwnerId,
            fullDate = fullDate,
            timeSpend = timeStart,
            secondsPassed = secondsFromLast,
            intervalSeconds = intervalSeconds,
            secondsToNext = secondsToNext
        )
    }

    fun addNewActivity(
        occurenceOwnerId: Int,
        fullDate: String,
        timeSpend: String,
        secondsFromLast: Long,
        intervalSeconds: Long,
        secondsToNext: Long
    ) {
        val newDateTime = getNewActivity(
            occurenceOwnerId,
            fullDate,
            timeSpend,
            secondsFromLast,
            intervalSeconds,
            secondsToNext
        )
        insertActivity(newDateTime)
    }

    fun getDate(): String {
        val currentDateTime = LocalDateTime.now()
        return currentDateTime.format(DateTimeFormatter.ofPattern(Constants.DEFAULT_FORMATTER))
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
            repository.dataSource.insertDescription(description)
        }
    }

    fun deleteDescription(description: Description) {
        viewModelScope.launch {
            repository.dataSource.deleteDescription(description)
        }
    }

    private fun getNewDescriptionEntry(
        descriptionNote: String,
        occurenceOwnerId: Int
    ): Description {
        return Description(
            descriptionDate = LocalDateTime.now().toString(),
            descriptionNote = descriptionNote,
            occurrenceOwnerId = occurenceOwnerId
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