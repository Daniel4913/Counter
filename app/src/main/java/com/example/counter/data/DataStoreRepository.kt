package com.example.counter.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.counter.util.Constants.Companion.NO_CATEGORY_DEFAULT
import com.example.counter.util.Constants.Companion.PREFERENCES_CATEGORY_CHIP
import com.example.counter.util.Constants.Companion.PREFERENCES_CATEGORY_CHIP_ID
import com.example.counter.util.Constants.Companion.PREFERENCES_CATEGORY_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(PREFERENCES_CATEGORY_NAME)

class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferenceKeys {
        val filterCategoryChip = stringPreferencesKey(PREFERENCES_CATEGORY_CHIP)
        val filterCategoryChipId = intPreferencesKey(PREFERENCES_CATEGORY_CHIP_ID)
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
            val filteredCategory = preferences[PreferenceKeys.filterCategoryChip] ?: NO_CATEGORY_DEFAULT
            val filteredCategoryChipId = preferences[PreferenceKeys.filterCategoryChipId] ?: 0
            FilterCategory(
                filteredCategory,
                filteredCategoryChipId
            )

        }

    data class FilterCategory(
        val filteredCategoryChip: String,
        val filteredCategoryChipId: Int
    )

}