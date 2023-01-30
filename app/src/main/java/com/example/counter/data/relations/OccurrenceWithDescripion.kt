package com.example.counter.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.counter.data.modelentity.Description
import com.example.counter.data.modelentity.Occurrence

data class OccurrenceWithDescripion (
    @Embedded
    val occurrence: Occurrence,

    @Relation(
        parentColumn = "occurrence_id",
        entityColumn = "occurrence_owner_id"
    )
    val occurrenceDescriptions: List<Description>
    ){}
