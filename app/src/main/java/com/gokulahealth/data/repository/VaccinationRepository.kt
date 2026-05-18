package com.gokulahealth.data.repository

import androidx.lifecycle.LiveData
import com.gokulahealth.data.dao.VaccinationDao
import com.gokulahealth.data.entity.Vaccination

class VaccinationRepository(private val vaccinationDao: VaccinationDao) {

    val allVaccinations: LiveData<List<Vaccination>> = vaccinationDao.getAllVaccinations()

    fun getVaccinationsForCattle(cattleId: Long): LiveData<List<Vaccination>> =
        vaccinationDao.getVaccinationsForCattle(cattleId)

    fun getUpcomingVaccinations(today: Long): LiveData<List<Vaccination>> =
        vaccinationDao.getUpcomingVaccinations(today)

    suspend fun getAllVaccinationsSync(): List<Vaccination> =
        vaccinationDao.getAllVaccinationsSync()

    suspend fun insert(vaccination: Vaccination): Long = vaccinationDao.insert(vaccination)

    suspend fun update(vaccination: Vaccination) = vaccinationDao.update(vaccination)

    suspend fun delete(vaccination: Vaccination) = vaccinationDao.delete(vaccination)
}
