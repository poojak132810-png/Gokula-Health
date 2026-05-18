package com.gokulahealth.data.repository

import androidx.lifecycle.LiveData
import com.gokulahealth.data.dao.MilkEntryDao
import com.gokulahealth.data.entity.MilkEntry

class MilkRepository(private val milkEntryDao: MilkEntryDao) {

    fun getEntriesForCattle(cattleId: Long): LiveData<List<MilkEntry>> =
        milkEntryDao.getEntriesForCattle(cattleId)

    fun getEntriesSince(cattleId: Long, startDate: Long): LiveData<List<MilkEntry>> =
        milkEntryDao.getEntriesSince(cattleId, startDate)

    fun getAverageYield(cattleId: Long, startDate: Long): LiveData<Float?> =
        milkEntryDao.getAverageYield(cattleId, startDate)

    fun getTotalYieldSince(cattleId: Long, startDate: Long): LiveData<Float?> =
        milkEntryDao.getTotalYieldSince(cattleId, startDate)

    fun getEntryCountSince(cattleId: Long, startDate: Long): LiveData<Int?> =
        milkEntryDao.getEntryCountSince(cattleId, startDate)

    suspend fun getEntriesSinceSync(cattleId: Long, startDate: Long): List<MilkEntry> =
        milkEntryDao.getEntriesSinceSync(cattleId, startDate)

    suspend fun insert(milkEntry: MilkEntry): Long = milkEntryDao.insert(milkEntry)

    suspend fun update(milkEntry: MilkEntry) = milkEntryDao.update(milkEntry)

    suspend fun delete(milkEntry: MilkEntry) = milkEntryDao.delete(milkEntry)
}
