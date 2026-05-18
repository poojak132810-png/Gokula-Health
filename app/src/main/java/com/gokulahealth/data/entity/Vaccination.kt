package com.gokulahealth.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vaccinations",
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
data class Vaccination(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cattleId: Long,
    val vaccineName: String,
    val dateGiven: Long,
    val nextDueDate: Long,
    val notes: String = ""
)
