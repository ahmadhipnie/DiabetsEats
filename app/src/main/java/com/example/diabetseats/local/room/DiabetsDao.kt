package com.example.diabetseats.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.diabetseats.local.entity.IndexRasioEntity
import com.example.diabetseats.local.entity.MakananEntity
import com.example.diabetseats.local.entity.NutrisiEntity
import com.example.diabetseats.local.entity.PerbandinganKriteriaEntity
import com.example.diabetseats.local.entity.PvKriteriaEntity
import com.example.diabetseats.local.entity.UserEntity
import com.example.diabetseats.local.repo.NutrientData


@Dao
interface DiabetsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(UserEntity: UserEntity)

    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password")
    fun login(email: String, password: String): UserEntity


    @Query("SELECT * FROM user_table WHERE email = :email ")
    fun checkRegister(email: String): UserEntity

    @Query("UPDATE user_table SET password = :password WHERE email = :email")
    fun updatePassword(email: String, password: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFood(makananEntity: MakananEntity)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRasio(indexRasioEntity: IndexRasioEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerbandinganKriteriaEntity(perbandinganKriteriaEntity: PerbandinganKriteriaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPvKriteria(pvKriteriaEntity: PvKriteriaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNutrisiEntity(nutrisiEntity: NutrisiEntity)

    @Query("SELECT * FROM makanan_table LIMIT :limit")
    fun getDataWithLimit(limit: Int): List<MakananEntity>

    @Query("SELECT * FROM makanan_table")
    fun getAllFood(): LiveData<List<MakananEntity>>

    @Delete
    fun delete(makananEntity: MakananEntity)


    @Query("SELECT nama_makanan, karbohidrat_makanan, protein_makanan, lemak_makanan FROM makanan_table")
    fun getNutrientData(): LiveData<List<NutrientData>>

    @Query("SELECT nilai FROM pv_kriteria")
    suspend fun getPvKriteriaValues(): List<Float>


    @Query("SELECT * FROM nutrisi_table where jenis_kelamin = :jenisKelamin and usia = :usia")
    fun getNutrisiByGenderAndAge(jenisKelamin: String, usia: String): LiveData<NutrisiEntity>

    @Query("UPDATE user_table SET nomor_hp = :newNomorHp, usia = :newUsia, tinggi_badan = :newTinggiBadan, berat_badan = :newBeratBadan WHERE email = :email")
    fun updateUserDetails(email: String, newNomorHp: String, newUsia: Int, newTinggiBadan: Int, newBeratBadan: Int)

    @Query("SELECT * FROM user_table WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?



}