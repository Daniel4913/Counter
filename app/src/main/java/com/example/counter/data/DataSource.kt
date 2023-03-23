package com.example.counter.data

import com.example.counter.data.modelentity.EventLog
import com.example.counter.data.modelentity.Event
import com.example.counter.data.relations.EventWithEventLogs
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataSource @Inject constructor(
    private val eventDao: EventDao,
    private val eventLogDao: EventLogDao,
    ) {
    // Occurrence access
    fun getOccurrences(): Flow<List<Event>> {
        return eventDao.getAllEvents()
    }

    fun getOccurrence(id: Int): Flow<Event> {
        return eventDao.getEvent(id)
    }

    fun getAllEventsWithEventLogs(): Flow<List<EventWithEventLogs>> {
        return eventDao.getAllEventsWithEventLogs()
    }

    fun getEventsByCategory(category: String): Flow<List<EventWithEventLogs>> {
        return eventDao.getEventsByCategory(category)
    }

    suspend fun insertEvent(event: Event) {
        eventDao.insertEvent(event)
    }

    suspend fun updateEvent(event: Event) {
        eventDao.updateEvent(event)
    }

    suspend fun deleteEvent(event: Event) {
        eventDao.delete(event)
    }

    suspend fun deleteAllEvents() {
        eventDao.deleteAll()
    }

    // Activity access

    fun getEventWithEventLogs(occurrenceId: Int): Flow<List<EventLog>>{
        return eventLogDao.getEventWithEventLogs(occurrenceId)
    }

    suspend fun insertEventLog(eventLog: EventLog){
        eventLogDao.insertEventLog(eventLog)
    }
    fun getActivity(id: Int): Flow<EventLog> {
        return eventLogDao.getEventLog(id)
    }

    suspend fun updateActivity(eventLog: EventLog){
        eventLogDao.update(eventLog)
    }

    suspend fun deleteActivity(eventLog: EventLog){
        eventLogDao.deleteEventLog(eventLog)
    }

    suspend fun deleteSingleEventLogs(occurrenceOwnerId: Int){
        eventLogDao.deleteEventLogs(occurrenceOwnerId)
    }

    suspend fun deleteAllEventLogs(){
        eventLogDao.deleteAllEventLogs()
    }

}