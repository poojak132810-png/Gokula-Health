package com.gokulahealth.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "milk_entries",
    foreignKeys = [
        ForeignKey(
            entity = Cattle::class,
            parentColumns = ["id"],
            childColumns = ["cattleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cattleId"])]
)
data class MilkEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cattleId: Long,
    val date: Long,
    val morningYield: Float,
    val eveningYield: Float,
    val totalYield: Float = morningYield + eveningYield
)
