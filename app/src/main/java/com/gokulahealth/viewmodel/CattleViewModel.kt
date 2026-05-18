package com.gokulahealth.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.gokulahealth.data.database.AppDatabase
import com.gokulahealth.data.entity.Cattle
import com.gokulahealth.data.repository.CattleRepository
import kotlinx.coroutines.launch

class CattleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CattleRepository
    val allCattle: LiveData<List<Cattle>>

    init {
        val cattleDao = AppDatabase.getDatabase(application).cattleDao()
        repository = CattleRepository(cattleDao)
        allCattle = repository.allCattle
    }

    fun getCattleById(id: Long): LiveData<Cattle?> = repository.getCattleById(id)

    fun insert(cattle: Cattle, onResult: (Long) -> Unit = {}) = viewModelScope.launch {
        val id = repository.insert(cattle)
        onResult(id)
    }

    fun update(cattle: Cattle) = viewModelScope.launch {
        repository.update(cattle)
    }

    fun delete(cattle: Cattle) = viewModelScope.launch {
        repository.delete(cattle)
    }

    suspend fun getAllCattleSync(): List<Cattle> = repository.getAllCattleSync()
}
