package com.example.diabetseats.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "makanan_table")
data class MakananEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_makanan")
    var idMakanan: Int = 0,

    @ColumnInfo(name = "nama_makanan")
    var namaMakanan: String,

    @ColumnInfo(name = "kalori_makanan")
    var kaloriMakanan: Float,

    @ColumnInfo(name = "protein_makanan")
    var proteinMakanan: Float,

    @ColumnInfo(name = "lemak_makanan")
    var lemakMakanan: Float,

    @ColumnInfo(name = "karbohidrat_makanan")
    var karbohidratMakanan: Float,

    @ColumnInfo(name = "image")
    var image: String

)