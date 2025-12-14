package com.example.romeojtask.data.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "holdings")
@TypeConverters(HoldingDetailsConverter::class)
data class HoldingEntity(
    @PrimaryKey
    val symbol: String,

    @Embedded
    val details: HoldingDetails
)
