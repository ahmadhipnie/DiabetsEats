package com.example.diabetseats.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrisi_table")
data class NutrisiEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_nutrisi")
    var idNutrisi: Int = 0,

    @ColumnInfo(name = "jenis_kelamin")
    var jenisKelamin: String,

    @ColumnInfo(name = "usia")
    var usia: String,

    @ColumnInfo(name = "lemak_harian")
    var lemakHarian: Int,

    @ColumnInfo(name = "protein_harian")
    var proteinHarian: Int,

    @ColumnInfo(name = "karbohidrat_harian")
    var karbohidratHarian: Int,
    )