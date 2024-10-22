package com.example.android.advancedcoroutines.domain.repository

import com.example.android.advancedcoroutines.core.CacheOnSuccess
import com.example.android.advancedcoroutines.domain.model.GrowZone
import com.example.android.advancedcoroutines.domain.model.Plant
import kotlinx.coroutines.flow.Flow

interface IPlantRepository {
    fun getPlants(): Flow<List<Plant>>

    fun getPlansWithGrowZoneFlow(growZone: GrowZone): Flow<List<Plant>>

    fun getPlantsListSortOrderCache(): CacheOnSuccess<List<String>>

    suspend fun tryUpdateRecentPlantsCache()

    suspend fun tryUpdateRecentPlantsForGrowZoneCache(growZoneNumber: GrowZone)
}