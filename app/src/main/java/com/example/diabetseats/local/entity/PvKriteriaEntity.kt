package com.example.diabetseats.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pv_kriteria")
data class PvKriteriaEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_kriteria")
    var idKriteria: Int = 0,

    @ColumnInfo(name = "nilai")
    var nilai: Float = 0.0F,
)