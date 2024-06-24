package com.sindi.diabetseats.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sindi.diabetseats.BuildConfig
import com.sindi.diabetseats.data.response.HintsItem
import com.sindi.diabetseats.data.response.MakananResponse
import com.sindi.diabetseats.data.retrofit.ApiConfig
import com.sindi.diabetseats.local.entity.MakananEntity
import com.sindi.diabetseats.local.room.DiabetsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MakananViewModel(private val dao: DiabetsDao) : ViewModel() {


    private val _makanan = MutableLiveData<List<HintsItem>>()
    val makanan: LiveData<List<HintsItem>> = _makanan

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _path = MutableStateFlow("")
    private val path: StateFlow<String> = _path

    init {
        viewModelScope.launch {
            path.collect {
                showFood()
            }
        }
    }

    suspend fun insertFood(makananEntity: MakananEntity) {
        withContext(Dispatchers.IO) {
            dao.insertFood(makananEntity)
        }
    }


    fun showFood() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDefaultFood()
        client.enqueue(object : Callback<MakananResponse> {
            override fun onResponse(
                call: Call<MakananResponse>,
                response: Response<MakananResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val makananResponse = response.body()
                    makananResponse?.let {
                        _makanan.value = it.hints
                    }
                } else {
                    Log.e("ResultViewModel", "onResponse: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<MakananResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("MainViewModel", "onFailure: ${t.message}", t)
            }
        })
    }

    fun showSearchedFood(foodName: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService()
            .getSearchedFood(BuildConfig.app_id, BuildConfig.app_key, foodName)
        client.enqueue(object : Callback<MakananResponse> {
            override fun onResponse(
                call: Call<MakananResponse>,
                response: Response<MakananResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val makananResponse = response.body()
                    makananResponse?.let {
                        _makanan.value = it.hints
                    }
                } else {
                    Log.e("ResultViewModel", "onResponse: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<MakananResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("MainViewModel", "onFailure: ${t.message}", t)
            }
        })
    }


}