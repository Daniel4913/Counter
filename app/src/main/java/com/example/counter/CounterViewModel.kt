package com.example.counter

import androidx.lifecycle.*
import com.example.counter.data.Occurence
import com.example.counter.data.OccurenceDao
import kotlinx.coroutines.launch

class CounterViewModel(private val occurenceDao: OccurenceDao) : ViewModel() {

    val allOccurences: LiveData<List<Occurence>> = occurenceDao.getOccurencies().asLiveData()

    fun retrieveOccurence(id: Int): LiveData<Occurence>{
        return occurenceDao.getOccurence(id).asLiveData()
    }

//    To interact with the database off the main thread, start a coroutine and call the DAO method within it
    private fun insertOccurence(occurence: Occurence) {
        viewModelScope.launch {
            occurenceDao.insert(occurence) }
    }

//  function that takes in strings and boolean and returns an Occurence instance.
    private fun getNewOccurenceEntry(
        occurenceName: String,
        occurenceDate: String,
        occurMore: Boolean,
        category: String
    ): Occurence {
        return Occurence(
            occurenceName = occurenceName,
            createDate = occurenceDate,
            occurMore = occurMore,
//            occurMore = occurMore.toString(), = drze pape ze type mismatch, chyba pojebalem strony co do czego XD
            category = category
//        occurenceName = name, = Cannot find a parameter with this name: occurenceName . Czyli dobre strony jednak
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
    fun isEntryValid(occurenceName: String, occurenceDate: String, category: String): Boolean {
        if (occurenceName.isBlank() || occurenceDate.isBlank() || category.isBlank()) {
            return false
        }
        return true
    }
}



/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class CounterViewModelFactory(private val occurenceDao: OccurenceDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CounterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CounterViewModel(occurenceDao) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}