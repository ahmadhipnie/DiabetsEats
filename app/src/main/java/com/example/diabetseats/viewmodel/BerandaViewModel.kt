package com.example.diabetseats.viewmodel
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diabetseats.local.entity.MakananEntity
import com.example.diabetseats.local.entity.NutrisiEntity
import com.example.diabetseats.local.repo.NutrientData
import com.example.diabetseats.local.room.DiabetsDao
import com.example.diabetseats.local.room.DiabetsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BerandaViewModel(private val context: Context) : ViewModel() {
    private val diabetsDao: DiabetsDao = DiabetsDatabase.getDatabase(context).diabetsDao()


    fun getAllFood(): LiveData<List<MakananEntity>> {
        return diabetsDao.getAllFood()
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


}
