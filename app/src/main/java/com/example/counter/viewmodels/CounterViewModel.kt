package com.example.counter.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.counter.data.*
//import com.example.counter.data.DataStoreRepository
//import com.example.counter.data.DataStoreRepository.FilterCategory
import com.example.counter.data.modelentity.EventLog
import com.example.counter.data.modelentity.Category
import com.example.counter.data.modelentity.CounterStatus
import com.example.counter.data.modelentity.Event
import com.example.counter.data.relations.EventWithEventLogs
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
//    private val notificationBuilder: NotificationCompat.Builder,
//    private val notificationManager: NotificationManagerCompat,
    application: Application
) : AndroidViewModel(application) {


    val occurrence = repository.dataSource.getAllEventsWithEventLogs().asLiveData()

    val readAllOccurrencesWithActivities: LiveData<List<EventWithEventLogs>> =
        repository.dataSource.getAllEventsWithEventLogs().asLiveData()

    fun getOccurrence(id: Int): LiveData<Event> {
        return repository.dataSource.getOccurrence(id).asLiveData()
    }

    fun getOccurrencesByCategory(category: String): LiveData<List<EventWithEventLogs>> {
        return repository.dataSource.getEventsByCategory(category).asLiveData()
    }

    fun getActivities(occurrenceId: Int): LiveData<List<EventLog>> {
        return repository.dataSource.getEventWithEventLogs(occurrenceId).asLiveData()
    }

    fun updateActivity(eventLog: EventLog) {
        viewModelScope.launch(Dispatchers.IO) { repository.dataSource.updateActivity(eventLog) }
    }

    private fun insertOccurrence(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dataSource.insertEvent(event)
        }
    }

    fun deleteOccurrence(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dataSource.deleteEvent(event)
            repository.dataSource.deleteSingleEventLogs(event.eventId)
        }
    }

    fun deleteAllOccurrences() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dataSource.deleteAllEvents()
            repository.dataSource.deleteAllEventLogs()
        }
    }

    private fun updateEvent(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dataSource.updateEvent(event)
        }
    }

    fun addNewOccurrence(
        occurrenceIcon: String,
        occurrenceName: String,
        createDate: String,
        category: Category,
        intervalFrequency: String
    ) {
        val newEvent = Event(
            eventIcon = occurrenceIcon,
            eventName = occurrenceName,
            createDate = createDate,
            category = category,
            intervalFrequency = intervalFrequency
        )
        insertOccurrence(newEvent)
    }

    fun updateEvent(
        occurrenceId: Int,
        occurrenceIcon: String,
        occurrenceName: String,
        createDate: String,
        category: Category,
        intervalFrequency: String,
        status: CounterStatus
    ) {
        val updatedEvent = Event(
            eventId = occurrenceId,
            eventIcon = occurrenceIcon,
            eventName = occurrenceName,
            createDate = createDate,
            category = category,
            intervalFrequency = intervalFrequency,
            status = status
        )
        updateEvent(updatedEvent)
    }

    fun setEventStatus(secondsTo: Long?, intervalSeconds: Long, event: Event) {
        var newStatus: CounterStatus? = if (secondsTo!! < 0) {
            CounterStatus.Late
        } else if (secondsTo!! < (intervalSeconds * 0.2)) {
            CounterStatus.CloseTo
        } else {
            CounterStatus.Enough
        }
        updateEvent(event.apply { status = newStatus })
    }

    fun isEntryValid(occurrenceName: String): Boolean {
        if (occurrenceName.isBlank()) {
            return false
        }
        return true
    }

    // DATES TIMES BLOCK
    private fun insertEventLog(eventLog: EventLog) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dataSource.insertEventLog(eventLog)
        }
    }

    fun deleteEventLog(eventLog: EventLog) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dataSource.deleteActivity(eventLog)
        }
    }

    fun getEventLog(id: Int): LiveData<EventLog> {
        return repository.dataSource.getActivity(id).asLiveData()
    }

    private fun getNewEventLog(
        occurrenceOwnerId: Int,
        fullDate: String,
        secondsFromLast: Long,
        intervalSeconds: Long,
        secondsToNext: Long,

        ): EventLog {
        return EventLog(
            eventOwnerId = occurrenceOwnerId,
            fullDate = fullDate,
            secondsPassed = secondsFromLast,
            intervalSeconds = intervalSeconds,
            secondsToNext = secondsToNext
        )
    }

    fun addNewEventLog(
        occurrenceOwnerId: Int,
        fullDate: String,
        secondsFromLast: Long,
        intervalSeconds: Long,
        secondsToNext: Long
    ) {
        val newDateTime = getNewEventLog(
            occurrenceOwnerId,
            fullDate,
            secondsFromLast,
            intervalSeconds,
            secondsToNext
        )
        insertEventLog(newDateTime)
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