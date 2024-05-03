package com.example.diabetseats.data.retrofit

import com.example.diabetseats.BuildConfig
import com.example.diabetseats.data.response.MakananResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("parser")
    fun getFood(
        @Query("app_id") appId: String = BuildConfig.app_id,
        @Query("app_key") appKey: String = BuildConfig.app_key,
    ): Call<MakananResponse>


    @GET("parser")
    fun getSearchedFood(
        @Query("app_id") appId: String?,
        @Query("app_key") appKey: String?,
        @Query("ingr") foodName: String?
    ): Call<MakananResponse>
}