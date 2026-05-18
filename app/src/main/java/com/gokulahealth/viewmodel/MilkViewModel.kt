package com.gokulahealth.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.gokulahealth.data.database.AppDatabase
import com.gokulahealth.data.entity.MilkEntry
import com.gokulahealth.data.repository.MilkRepository
import kotlinx.coroutines.launch

class MilkViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MilkRepository

    init {
        val milkEntryDao = AppDatabase.getDatabase(application).milkEntryDao()
        repository = MilkRepository(milkEntryDao)
    }

    private val _selectedCattleId = MutableLiveData<Long>()
    val selectedCattleId: LiveData<Long> = _selectedCattleId

    val entriesForSelectedCattle: LiveData<List<MilkEntry>> = _selectedCattleId.switchMap { cattleId ->
        repository.getEntriesForCattle(cattleId)
    }

    private val _thirtyDaysAgo = MutableLiveData<Pair<Long, Long>>()

    val entriesLast30Days: LiveData<List<MilkEntry>> = _thirtyDaysAgo.switchMap { (cattleId, startDate) ->
        repository.getEntriesSince(cattleId, startDate)
    }

    val averageYield: LiveData<Float?> = _thirtyDaysAgo.switchMap { (cattleId, startDate) ->
        repository.getAverageYield(cattleId, startDate)
    }

    val totalYield: LiveData<Float?> = _thirtyDaysAgo.switchMap { (cattleId, startDate) ->
        repository.getTotalYieldSince(cattleId, startDate)
    }

    val entryCount: LiveData<Int?> = _thirtyDaysAgo.switchMap { (cattleId, startDate) ->
        repository.getEntryCountSince(cattleId, startDate)
    }

    fun selectCattle(cattleId: Long) {
        _selectedCattleId.value = cattleId
        val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        _thirtyDaysAgo.value = Pair(cattleId, thirtyDaysAgo)
    }

    fun insert(milkEntry: MilkEntry) = viewModelScope.launch {
        repository.insert(milkEntry)
    }

    fun update(milkEntry: MilkEntry) = viewModelScope.launch {
        repository.update(milkEntry)
    }

    fun delete(milkEntry: MilkEntry) = viewModelScope.launch {
        repository.delete(milkEntry)
    }

    suspend fun getEntriesSinceSync(cattleId: Long, startDate: Long): List<MilkEntry> =
        repository.getEntriesSinceSync(cattleId, startDate)
}
