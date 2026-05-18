package com.gokulahealth.data.repository

import androidx.lifecycle.LiveData
import com.gokulahealth.data.dao.CattleDao
import com.gokulahealth.data.entity.Cattle

class CattleRepository(private val cattleDao: CattleDao) {

    val allCattle: LiveData<List<Cattle>> = cattleDao.getAllCattle()

    fun getCattleById(id: Long): LiveData<Cattle?> = cattleDao.getCattleById(id)

    suspend fun getCattleByIdSync(id: Long): Cattle? = cattleDao.getCattleByIdSync(id)

    suspend fun getAllCattleSync(): List<Cattle> = cattleDao.getAllCattleSync()

    suspend fun insert(cattle: Cattle): Long = cattleDao.insert(cattle)

    suspend fun update(cattle: Cattle) = cattleDao.update(cattle)

    suspend fun delete(cattle: Cattle) = cattleDao.delete(cattle)
}
