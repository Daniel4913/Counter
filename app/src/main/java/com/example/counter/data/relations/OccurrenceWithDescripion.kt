package com.example.counter.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.counter.data.Description
import com.example.counter.data.Occurence

data class OccurrenceWithDescripion (
    @Embedded
    val occurence: Occurence,

    @Relation(
        parentColumn = "occurence_id",
        entityColumn = "occurence_owner_id"
    )
    val description: List<Description>
    ){}
