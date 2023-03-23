package com.example.counter.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.counter.data.modelentity.EventLog
import com.example.counter.data.modelentity.Event

data class EventWithEventLogs (
    @Embedded
    val event: Event,

    @Relation(
        parentColumn = "eventId",
        entityColumn = "event_owner_id"
    )
    val singleEventEventLogs: List<EventLog>
)