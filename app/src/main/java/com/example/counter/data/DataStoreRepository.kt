package com.example.counter.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("filter_by_category_preferences")

class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferenceKeys {
        val filterCategoryChip = stringPreferencesKey("filteredCategory")
        val filterCategoryChipId = intPreferencesKey("filteredCategoryId")
    }

    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun saveFilterCategory(categoryChip: String, categoryChipId: Int) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.filterCategoryChip] = categoryChip
            preferences[PreferenceKeys.filterCategoryChipId] = categoryChipId
        }
    }

    val readFilterCategory = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val filteredCategory = preferences[PreferenceKeys.filterCategoryChip] ?: "All"
            val filteredCategoryChipId = preferences[PreferenceKeys.filterCategoryChipId] ?: 0
            FilterCategory(
                filteredCategory,
                filteredCategoryChipId
            )

        }

    data class FilterCategory(
        val filteredCategoryChip: String,
        val filteredCategoryChipId: Int
//        val all: String,
//        val substancje: String,
//        val relacje: String,
//        val rozwoj: String,
//        val trening: String,
//        val obowiazki: String,
    )

}