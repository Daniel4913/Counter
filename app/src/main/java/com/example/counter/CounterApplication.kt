package com.example.counter

import android.app.Application
import com.example.counter.data.CounterRoomDatabase


class CounterApplication: Application() {
    val database: CounterRoomDatabase by lazy { CounterRoomDatabase.getDatabase(this)}
}