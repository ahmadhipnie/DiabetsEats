package com.sindi.diabetseats.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perbandingan_kriteria")
data class PerbandinganKriteriaEntity (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id_kriteria")
    var idKriteria: Int = 0,

    @ColumnInfo(name = "kriteria1")
    var kriteria1: Int = 0,

    @ColumnInfo(name = "kriteria2")
    var kriteria2: Int = 0,

    @ColumnInfo(name = "nilai")
    var nilai: Float = 0.0F,
)