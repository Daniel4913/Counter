package com.example.counter.util

class Constants {

    companion object {

        // DB
        const val DATABASE_NAME = "counter_database"
        const val OCCURRENCES_TABLE = "occurrences_table"
        const val ACTIVITIES_TABLE = "activities_table"
        const val DESCRIPTIONS_TABLE = "descriptions_table"

        // DataStore
        const val PREFERENCES_CATEGORY_NAME = "filter_by_category_preferences"
        const val PREFERENCES_CATEGORY_CHIP = "filteredCategory"
        const val PREFERENCES_CATEGORY_CHIP_ID = "filteredCategory"

        // Category
        const val NO_CATEGORY_DEFAULT = "All"

        // Number Picker
        const val DEFAULT_DAYS = 0
        const val DEFAULT_MAX_DAYS = 365

        const val DEFAULT_HOURS = 0
        const val DEFAULT_MAX_HOURS = 42

        const val DEFAULT_MINUTES = 0
        const val DEFAULT_MAX_MINUTES = 120

        // Frequency Chips
        const val MINUTES = "minutes"
        const val HOURS = "hours"
        const val DAYS = "days"
        const val WEEKS = "weeks"
        const val MONTHS = "month"

        // Date Time
        const val DEFAULT_FORMATTER = "HH:mm:ss dd.MM.yyyy"

    }


}