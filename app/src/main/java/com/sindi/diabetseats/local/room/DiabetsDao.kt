package com.sindi.diabetseats.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sindi.diabetseats.local.entity.IndexRasioEntity
import com.sindi.diabetseats.local.entity.MakananEntity
import com.sindi.diabetseats.local.entity.NutrisiEntity
import com.sindi.diabetseats.local.entity.PerbandinganKriteriaEntity
import com.sindi.diabetseats.local.entity.PvKriteriaEntity
import com.sindi.diabetseats.local.entity.UserEntity
import com.sindi.diabetseats.local.repo.NutrientData


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



    @Query("SELECT * FROM makanan_table WHERE waktu_makan = 'pagi'")
    fun getMorningFood(): LiveData<List<MakananEntity>>

    @Query("SELECT * FROM makanan_table WHERE waktu_makan = 'siang'")
    fun getAfternoonFood(): LiveData<List<MakananEntity>>

    @Query("SELECT * FROM makanan_table WHERE waktu_makan = 'malam'")
    fun getEveningFood(): LiveData<List<MakananEntity>>

    @Query("SELECT * FROM makanan_table WHERE waktu_makan = 'snack'")
    fun getSnackFood(): LiveData<List<MakananEntity>>



    @Delete
    fun delete(makananEntity: MakananEntity)


    @Query("SELECT nama_makanan, karbohidrat_makanan, protein_makanan, lemak_makanan FROM makanan_table")
    fun getNutrientData(): LiveData<List<NutrientData>>

    @Query("SELECT nilai FROM pv_kriteria")
    suspend fun getPvKriteriaValues(): List<Float>


    @Query("SELECT * FROM nutrisi_table where jenis_kelamin = :jenisKelamin and usia = :usia")
    fun getNutrisiByGenderAndAge(jenisKelamin: String, usia: String): LiveData<NutrisiEntity>

    @Query("UPDATE user_table SET  tanggal_lahir = :newTanggalLahir, tinggi_badan = :newTinggiBadan, berat_badan = :newBeratBadan WHERE email = :email")
    fun updateUserDetails(email: String, newTanggalLahir: String, newTinggiBadan: Int, newBeratBadan: Int)

    @Query("SELECT * FROM user_table WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT SUM(kalori_makanan) AS totalKalori FROM makanan_table")
    fun getTotalCalories(): LiveData<Float>






}