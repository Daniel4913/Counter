package com.example.counter.util

import java.time.format.DateTimeFormatter

class Constants {

    companion object {

        // DB
        const val DATABASE_NAME = "counter_database"
        const val OCCURRENCES_TABLE = "occurrences_table"
        const val ACTIVITIES_TABLE = "activities_table"
        const val DESCRIPTIONS_TABLE = "descriptions_table"

        // Number Picker
        const val DEFAULT_DAYS = 0
        const val DEFAULT_MAX_DAYS = 365

        const val DEFAULT_HOURS = 0
        const val DEFAULT_MAX_HOURS = 42

        const val DEFAULT_MINUTES = 0
        const val DEFAULT_MAX_MINUTES = 120
        // TODO mozna by zrobic pozniej STEP minut co ilestam :')


        // Frequency Chips
        const val MINUTES = "minutes"
        const val HOURS = "hours"
        const val DAYS = "days"
        const val WEEKS = "weeks"
        const val MONTHS = "month"

    }


}