package com.gokulahealth.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gokulahealth.data.dao.CattleDao
import com.gokulahealth.data.dao.MilkEntryDao
import com.gokulahealth.data.dao.VaccinationDao
import com.gokulahealth.data.entity.Cattle
import com.gokulahealth.data.entity.MilkEntry
import com.gokulahealth.data.entity.Vaccination

@Database(
    entities = [Cattle::class, MilkEntry::class, Vaccination::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cattleDao(): CattleDao
    abstract fun milkEntryDao(): MilkEntryDao
    abstract fun vaccinationDao(): VaccinationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gokula_health_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
