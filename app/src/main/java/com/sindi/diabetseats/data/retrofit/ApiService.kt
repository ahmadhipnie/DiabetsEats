package com.sindi.diabetseats.data.retrofit

import com.sindi.diabetseats.BuildConfig
import com.sindi.diabetseats.data.response.MakananResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("parser")
    fun getDefaultFood(
        @Query("app_id") appId: String = BuildConfig.app_id,
        @Query("app_key") appKey: String = BuildConfig.app_key,
        @Query("ingr") foodName: String = "nasi"
    ): Call<MakananResponse>

    @GET("parser")
    fun getSearchedFood(
        @Query("app_id") appId: String?,
        @Query("app_key") appKey: String?,
        @Query("ingr") foodName: String?
    ): Call<MakananResponse>
}