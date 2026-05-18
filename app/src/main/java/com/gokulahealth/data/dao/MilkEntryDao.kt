package com.gokulahealth.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gokulahealth.data.entity.MilkEntry

@Dao
interface MilkEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(milkEntry: MilkEntry): Long

    @Update
    suspend fun update(milkEntry: MilkEntry)

    @Delete
    suspend fun delete(milkEntry: MilkEntry)

    @Query("SELECT * FROM milk_entries WHERE cattleId = :cattleId ORDER BY date DESC")
    fun getEntriesForCattle(cattleId: Long): LiveData<List<MilkEntry>>

    @Query("SELECT * FROM milk_entries WHERE cattleId = :cattleId AND date >= :startDate ORDER BY date ASC")
    fun getEntriesSince(cattleId: Long, startDate: Long): LiveData<List<MilkEntry>>

    @Query("SELECT AVG(totalYield) FROM milk_entries WHERE cattleId = :cattleId AND date >= :startDate")
    fun getAverageYield(cattleId: Long, startDate: Long): LiveData<Float?>

    @Query("SELECT SUM(totalYield) FROM milk_entries WHERE cattleId = :cattleId AND date >= :startDate")
    fun getTotalYieldSince(cattleId: Long, startDate: Long): LiveData<Float?>

    @Query("SELECT COUNT(*) FROM milk_entries WHERE cattleId = :cattleId AND date >= :startDate")
    fun getEntryCountSince(cattleId: Long, startDate: Long): LiveData<Int?>

    @Query("SELECT * FROM milk_entries WHERE cattleId = :cattleId AND date >= :startDate ORDER BY date ASC")
    suspend fun getEntriesSinceSync(cattleId: Long, startDate: Long): List<MilkEntry>
}
