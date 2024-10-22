package com.example.android.advancedcoroutines

import com.example.android.advancedcoroutines.data.remote.SunflowerApi
import com.example.android.advancedcoroutines.domain.model.GrowZone
import com.example.android.advancedcoroutines.domain.model.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkServiceDeprecated {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val sunflowerApi = retrofit.create(SunflowerApi::class.java)

    suspend fun allPlants(): List<Plant> = withContext(Dispatchers.Default) {
        val result = sunflowerApi.getAllPlants()
        result.shuffled()
    }

    suspend fun plantsByGrowZone(growZone: GrowZone) = withContext(Dispatchers.Default) {
        val result = sunflowerApi.getAllPlants()
        result.filter { it.growZoneNumber == growZone.number }.shuffled()
    }

    suspend fun customPlantSortOrder(): List<String> = withContext(Dispatchers.Default) {
        val result = sunflowerApi.getCustomPlantSortOrder()
        result.map { plant -> plant.plantId }
    }
}