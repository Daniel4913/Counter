package com.example.counter.data

import com.example.counter.data.modelentity.Activity
import com.example.counter.data.modelentity.Occurrence
import com.example.counter.data.relations.OccurrenceWithActivities
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataSource @Inject constructor(
    private val occurrenceDao: OccurrenceDao,
    private val activityDao: ActivityDao,
    ) {
    // Occurrence access
    fun getOccurrences(): Flow<List<Occurrence>> {
        return occurrenceDao.getOccurrences()
    }

    fun getOccurrence(id: Int): Flow<Occurrence> {
        return occurrenceDao.getOccurrence(id)
    }

    fun getOccurrencesWithActivities(): Flow<List<OccurrenceWithActivities>> {
        return occurrenceDao.getOccurrencesWithActivities()
    }

    fun getOccurrencesByCategory(category: String): Flow<List<OccurrenceWithActivities>> {
        return occurrenceDao.getOccurrencesByCategory(category)
    }

    suspend fun insertOccurrence(occurrence: Occurrence) {
        occurrenceDao.insertOccurrence(occurrence)
    }

    suspend fun updateOccurrence(occurrence: Occurrence) {
        occurrenceDao.updateOccurrence(occurrence)
    }

    suspend fun deleteOccurrence(occurrence: Occurrence) {
        occurrenceDao.delete(occurrence)
    }

    suspend fun deleteAllOccurrences() {
        occurrenceDao.deleteAll()
    }

    // Activity access

    fun getAllActivities(): Flow<List<Activity>>{
      return  activityDao.getAllActivities()
    }

    fun getOccurrenceActivities(occurrenceId: Int): Flow<List<Activity>>{
        return activityDao.getOccurrenceWithActivities(occurrenceId)
    }

    suspend fun insertActivity(activity: Activity){
        activityDao.insertActivity(activity)
    }
    fun getActivity(id: Int): Flow<Activity> {
        return activityDao.getActivity(id)
    }

    suspend fun updateSeconds(activityId: Int, secondsFrom: Long, secondsTo: Long){
        activityDao.updateSeconds(activityId,secondsFrom,secondsTo)
    }

    suspend fun updateActivity(activity: Activity){
        activityDao.update(activity)
    }

    suspend fun deleteActivity(activity: Activity){
        activityDao.deleteAll(activity)
    }

    suspend fun deleteAllActivities(){
        activityDao.deleteAll()
    }

}