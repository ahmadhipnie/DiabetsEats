package com.example.diabetseats.local.repo

import androidx.room.ColumnInfo

data class NutrientData(
    @ColumnInfo(name = "nama_makanan")
    val namaMakanan: String, // Tambah properti namaMakanan

    @ColumnInfo(name = "karbohidrat_makanan")
    val karbohidratMakanan: Float,

    @ColumnInfo(name = "protein_makanan")
    val proteinMakanan: Float,

    @ColumnInfo(name = "lemak_makanan")
    val lemakMakanan: Float
)


