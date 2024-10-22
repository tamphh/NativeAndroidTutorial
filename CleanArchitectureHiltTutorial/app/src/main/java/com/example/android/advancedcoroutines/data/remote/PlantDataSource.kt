package com.example.android.advancedcoroutines.data.remote

import com.example.android.advancedcoroutines.domain.model.GrowZone
import com.example.android.advancedcoroutines.domain.model.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlantDataSource(private val sunflowerApi: SunflowerApi) {
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