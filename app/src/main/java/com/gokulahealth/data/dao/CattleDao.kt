package com.gokulahealth.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gokulahealth.data.entity.Cattle

@Dao
interface CattleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cattle: Cattle): Long

    @Update
    suspend fun update(cattle: Cattle)

    @Delete
    suspend fun delete(cattle: Cattle)

    @Query("SELECT * FROM cattle ORDER BY createdAt DESC")
    fun getAllCattle(): LiveData<List<Cattle>>

    @Query("SELECT * FROM cattle WHERE id = :id")
    fun getCattleById(id: Long): LiveData<Cattle?>

    @Query("SELECT * FROM cattle WHERE id = :id")
    suspend fun getCattleByIdSync(id: Long): Cattle?

    @Query("SELECT * FROM cattle ORDER BY name ASC")
    suspend fun getAllCattleSync(): List<Cattle>
}
