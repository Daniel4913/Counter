package com.example.counter

import android.app.Application
import com.example.counter.data.OccurencyRoomDatabase


class CounterApplication: Application() {
    val database: OccurencyRoomDatabase by lazy { OccurencyRoomDatabase.getDatabase(this)}
}