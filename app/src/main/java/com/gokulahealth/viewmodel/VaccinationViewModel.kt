package com.gokulahealth.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.gokulahealth.data.database.AppDatabase
import com.gokulahealth.data.entity.Vaccination
import com.gokulahealth.data.repository.VaccinationRepository
import kotlinx.coroutines.launch

class VaccinationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VaccinationRepository
    val allVaccinations: LiveData<List<Vaccination>>

    init {
        val vaccinationDao = AppDatabase.getDatabase(application).vaccinationDao()
        repository = VaccinationRepository(vaccinationDao)
        allVaccinations = repository.allVaccinations
    }

    fun getVaccinationsForCattle(cattleId: Long): LiveData<List<Vaccination>> =
        repository.getVaccinationsForCattle(cattleId)

    fun getUpcomingVaccinations(today: Long): LiveData<List<Vaccination>> =
        repository.getUpcomingVaccinations(today)

    fun insert(vaccination: Vaccination, onResult: (Long) -> Unit = {}) = viewModelScope.launch {
        val id = repository.insert(vaccination)
        onResult(id)
    }

    fun update(vaccination: Vaccination) = viewModelScope.launch {
        repository.update(vaccination)
    }

    fun delete(vaccination: Vaccination) = viewModelScope.launch {
        repository.delete(vaccination)
    }

    suspend fun getAllVaccinationsSync(): List<Vaccination> =
        repository.getAllVaccinationsSync()
}
