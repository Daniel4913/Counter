package com.example.counter.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.counter.data.*
//import com.example.counter.data.DataStoreRepository
//import com.example.counter.data.DataStoreRepository.FilterCategory
import com.example.counter.data.modelentity.Activity
import com.example.counter.data.modelentity.Occurrence
import com.example.counter.data.relations.OccurrenceWithActivities
import com.example.counter.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val repository: Repository,
//    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

//    private lateinit var filterCategory: FilterCategory

//    val readFilterCategory = dataStoreRepository.readFilterCategory


//    private fun saveFilterCategory(){
//        viewModelScope.launch(Dispatchers.IO) {
//            if(this@CounterViewModel::filterCategory.isInitialized){
//                dataStoreRepository.saveFilterCategory(
//                    filterCategory.filteredCategoryChip,
//                    filterCategory.filteredCategoryChipId
//                )
//            }
//        }
//    }
//
//    fun saveFilterCategoryTemp(
//        filterCategoryChip: String,
//        filterCategoryChipId: Int
//    ){
//        filterCategory = FilterCategory(
//            filterCategoryChip,
//            filterCategoryChipId
//        )
//        saveFilterCategory()
//    }


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

    private fun getNewOccurrenceEntry(
        occurrenceName: String,
        createDate: String,

        category: String,
        intervalFrequency: String
    ): Occurrence {
        return Occurrence(
            occurrenceName = occurrenceName,
            createDate = createDate,

            category = category,
            intervalFrequency = intervalFrequency
        )
    }

    fun addNewOccurrence(
        occurrenceName: String,
        createDate: String,

        category: String,
        intervalFrequency: String
    ) {
        val newOccurrence =
            getNewOccurrenceEntry(occurrenceName, createDate, category, intervalFrequency)
        insertOccurence(newOccurrence)
    }

    fun updateOccurrence(
        occurrenceId: Int,
        occurrenceName: String,
        createDate: String,

        category: String,
        intervalFrequency: String
    ) {
        val updatedOccurence = getUpdatedOccurrenceEntry(
            occurrenceId = occurrenceId,
            occurrenceName = occurrenceName,
            createDate = createDate,

            category = category,
            intervalFrequency = intervalFrequency
        )
        updateOccurrence(updatedOccurence)
    }

    private fun getUpdatedOccurrenceEntry(
        occurrenceId: Int,
        occurrenceName: String,
        createDate: String,
        category: String,
        intervalFrequency: String
    ): Occurrence {
        return Occurrence(
            occurrenceId = occurrenceId,
            occurrenceName = occurrenceName,
            createDate = createDate,

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

        secondsFromLast: Long,
        intervalSeconds: Long,
        secondsToNext: Long,

        ): Activity {
        return Activity(
            occurrenceOwnerId = occurenceOwnerId,
            fullDate = fullDate,

            secondsPassed = secondsFromLast,
            intervalSeconds = intervalSeconds,
            secondsToNext = secondsToNext
        )
    }

    fun addNewActivity(
        occurenceOwnerId: Int,
        fullDate: String,

        secondsFromLast: Long,
        intervalSeconds: Long,
        secondsToNext: Long
    ) {
        val newDateTime = getNewActivity(
            occurenceOwnerId,
            fullDate,

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

}