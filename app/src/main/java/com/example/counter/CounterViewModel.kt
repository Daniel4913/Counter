package com.example.counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.counter.data.Occurence
import com.example.counter.data.OccurenceDao
import kotlinx.coroutines.launch

class HomeViewModel(private val occurenceDao: OccurenceDao) : ViewModel() {
    private fun insertOccurence(occurence: Occurence) {
        viewModelScope.launch { occurenceDao.insert(occurence) }
    }

    private fun getNewOccurenceEntry(
        occurenceName: String,
        occurenceDate: String,
        occurMore: Boolean,
        category: String
    ): Occurence {
        return Occurence(
            name = occurenceName,
            createDate = occurenceDate,
            occurMore = occurMore,
            category = category
        )
    }

    fun addNewOccurence(
        occurenceName: String,
        occurenceDate: String,
        occurMore: Boolean,
        category: String
    ) {
        val newOccurence = getNewOccurenceEntry(occurenceName, occurenceDate, occurMore, category)
        insertOccurence(newOccurence)
    }
}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class HomeViewModelFactory(private val occurenceDao: OccurenceDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(occurenceDao) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}