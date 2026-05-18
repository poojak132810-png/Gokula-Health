package com.gokulahealth.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cattle",
    indices = [Index(value = ["earTagId"], unique = true)]
)
data class Cattle(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val earTagId: String,
    val name: String,
    val breed: String,
    val dateOfBirth: Long,
    val photoUri: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
