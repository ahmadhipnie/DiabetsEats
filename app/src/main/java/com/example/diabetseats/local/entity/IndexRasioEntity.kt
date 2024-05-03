package com.example.diabetseats.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "index_rasio_table")
data class IndexRasioEntity (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "jumlah")
    var jumlah: Int = 0,

    @ColumnInfo(name = "nilai")
    val nilai: Float = 0.0F,
)