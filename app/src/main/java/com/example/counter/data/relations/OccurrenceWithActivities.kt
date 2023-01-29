package com.example.counter.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.counter.data.Activity
import com.example.counter.data.Occurrence

data class OccurrenceWithActivities (
    @Embedded
    val occurrence: Occurrence,

    @Relation(
        parentColumn = "occurrence_id",
        entityColumn = "occurrence_owner_id"
    )
    val occurrenceActivities: List<Activity>
){
    companion object{

//        fun sortByDateTime(): Comparator<DateTime> = object :Comparator<DateTime>{
//            override fun compare(p0: DateTime?, p1: DateTime?): Int {
//                return p0!!.secondsToNext!!.compareTo(p1!!.secondsToNext!!)
//            }
//        }
    }
}