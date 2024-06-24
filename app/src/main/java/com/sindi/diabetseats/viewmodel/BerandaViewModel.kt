package com.sindi.diabetseats.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sindi.diabetseats.local.entity.MakananEntity
import com.sindi.diabetseats.local.entity.NutrisiEntity
import com.sindi.diabetseats.local.repo.NutrientData
import com.sindi.diabetseats.local.room.DiabetsDao
import com.sindi.diabetseats.local.room.DiabetsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BerandaViewModel(private val context: Context) : ViewModel() {
    private val diabetsDao: DiabetsDao = DiabetsDatabase.getDatabase(context).diabetsDao()


    fun getMorningFood(): LiveData<List<MakananEntity>> {
        return diabetsDao.getMorningFood()
    }

    fun getAfternoonFood(): LiveData<List<MakananEntity>> {
        return diabetsDao.getAfternoonFood()
    }

    fun getEveningFood(): LiveData<List<MakananEntity>> {
        return diabetsDao.getEveningFood()
    }

    fun getSnackFood(): LiveData<List<MakananEntity>> {
        return diabetsDao.getSnackFood()
    }

    fun getNutrisiDataByGenderAndAge(jenisKelamin: String, usia: String): LiveData<NutrisiEntity> {
        return diabetsDao.getNutrisiByGenderAndAge(jenisKelamin, usia)
    }


    fun deleteFood(makananEntity: MakananEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            diabetsDao.delete(makananEntity)
        }
    }

    fun getNutrientData(): LiveData<List<NutrientData>> {
        return diabetsDao.getNutrientData()
    }

    fun getTotalCalories(): LiveData<Float> {
        return diabetsDao.getTotalCalories()
    }


}
