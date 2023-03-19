package com.example.counter.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.counter.data.modelentity.Activity
import com.example.counter.data.modelentity.Occurrence

data class OccurrenceWithActivities (
    @Embedded
    val occurrence: Occurrence,

    @Relation(
        parentColumn = "occurrenceId",
        entityColumn = "occurrence_owner_id"
    )
    val occurrenceActivities: List<Activity>
)