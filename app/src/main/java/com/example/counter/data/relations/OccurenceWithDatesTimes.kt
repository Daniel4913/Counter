package com.example.counter.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.counter.data.DateTime
import com.example.counter.data.Occurence

data class OccurenceWithDatesTimes (
    @Embedded
    val occurence: Occurence,

    @Relation(
        parentColumn = "occurenceId",
        entityColumn = "occurenceOwnerId"
    )
    val datesTimes: List<DateTime>
){

}