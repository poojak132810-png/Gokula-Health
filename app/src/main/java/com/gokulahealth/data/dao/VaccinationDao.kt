package com.gokulahealth.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gokulahealth.data.entity.Vaccination

@Dao
interface VaccinationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vaccination: Vaccination): Long

    @Update
    suspend fun update(vaccination: Vaccination)

    @Delete
    suspend fun delete(vaccination: Vaccination)

    @Query("SELECT * FROM vaccinations WHERE cattleId = :cattleId ORDER BY nextDueDate ASC")
    fun getVaccinationsForCattle(cattleId: Long): LiveData<List<Vaccination>>

    @Query("SELECT * FROM vaccinations ORDER BY nextDueDate ASC")
    fun getAllVaccinations(): LiveData<List<Vaccination>>

    @Query("SELECT * FROM vaccinations WHERE nextDueDate >= :today ORDER BY nextDueDate ASC")
    fun getUpcomingVaccinations(today: Long): LiveData<List<Vaccination>>

    @Query("SELECT * FROM vaccinations ORDER BY nextDueDate ASC")
    suspend fun getAllVaccinationsSync(): List<Vaccination>
}
