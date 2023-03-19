package com.example.counter.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.counter.data.*
//import com.example.counter.data.DataStoreRepository
//import com.example.counter.data.DataStoreRepository.FilterCategory
import com.example.counter.data.modelentity.Activity
import com.example.counter.data.modelentity.Category
import com.example.counter.data.modelentity.CounterStatus
import com.example.counter.data.modelentity.Occurrence
import com.example.counter.data.relations.OccurrenceWithActivities
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
//    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    val occurrence = repository.dataSource.getOccurrencesWithActivities().asLiveData()

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
        viewModelScope.launch(Dispatchers.IO) { repository.dataSource.updateActivity(activity) }
    }


    private fun insertOccurrence(occurrence: Occurrence) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dataSource.insertOccurrence(occurrence)
        }
    }

    fun deleteOccurrence(occurrence: Occurrence) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dataSource.deleteOccurrence(occurrence)
        }
    }

    fun deleteAllOccurrences() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dataSource.deleteAllOccurrences()
        }
    }

    fun updateOccurrence(occurrence: Occurrence) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dataSource.updateOccurrence(occurrence)
        }
    }

    fun addNewOccurrence(
        occurrenceIcon: String,
        occurrenceName: String,
        createDate: String,
        category: Category,
        intervalFrequency: String
    ) {
        val newOccurrence = Occurrence(
            occurrenceIcon = occurrenceIcon,
            occurrenceName = occurrenceName,
            createDate = createDate,
            category = category,
            intervalFrequency = intervalFrequency
        )
        insertOccurrence(newOccurrence)
    }

    fun updateOccurrence(
        occurrenceId: Int,
        occurrenceIcon: String,
        occurrenceName: String,
        createDate: String,
        category: Category,
        intervalFrequency: String,
        status: CounterStatus
    ) {
        val updatedOccurence = Occurrence(
            occurrenceId = occurrenceId,
            occurrenceIcon = occurrenceIcon,
            occurrenceName = occurrenceName,
            createDate = createDate,
            category = category,
            intervalFrequency = intervalFrequency,
            status = status
        )
        updateOccurrence(updatedOccurence)
    }

    fun setOccurrenceStatus(secondsTo: Long?, intervalSeconds: Long, occurrence: Occurrence) {
        var newStatus: CounterStatus? = if (secondsTo!! < 0) {
            CounterStatus.Late
        } else if (secondsTo!! < (intervalSeconds * 0.2)) {
            CounterStatus.CloseTo
        } else {
            CounterStatus.Enough
        }

        updateOccurrence(occurrence.apply { status = newStatus })
    }

    fun isEntryValid(occurrenceName: String): Boolean {
        if (occurrenceName.isBlank()) {
            return false
        }
        return true
    }

    // DATES TIMES BLOCK
    private fun insertActivity(activity: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dataSource.insertActivity(activity)
        }
    }

    fun deleteActivity(activity: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
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