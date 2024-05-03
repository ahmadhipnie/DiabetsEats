package com.example.diabetseats.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_table")
data class UserEntity (
    @PrimaryKey(autoGenerate = false)
    var email: String,

    @ColumnInfo(name = "nama")
    var nama: String,

    @ColumnInfo(name = "password")
    var password: String,

    @ColumnInfo(name = "nomor_hp")
    var nomorHp: String,

    @ColumnInfo(name = "jenis_kelamin")
    var jenisKelamin: String,

    @ColumnInfo(name = "usia")
    var usia: Int,

    @ColumnInfo(name = "tinggi_badan")
    var tinggiBadan: Int,

    @ColumnInfo(name = "berat_badan")
    var beratBadan: Int,

)