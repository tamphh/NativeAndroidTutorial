package com.example.android.advancedcoroutines.data.repository

import androidx.annotation.AnyThread
import com.example.android.advancedcoroutines.core.CacheOnSuccess
import com.example.android.advancedcoroutines.core.ComparablePair
import com.example.android.advancedcoroutines.data.local.PlantDao
import com.example.android.advancedcoroutines.data.remote.PlantDataSource
import com.example.android.advancedcoroutines.data.remote.SunflowerApi
import com.example.android.advancedcoroutines.domain.model.GrowZone
import com.example.android.advancedcoroutines.domain.model.Plant
import com.example.android.advancedcoroutines.domain.repository.IPlantRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlantRepositoryImpl @Inject constructor (
    private val plantDao: PlantDao,
    sunflowerApi: SunflowerApi,
): IPlantRepository {

    private val plantDataSource = PlantDataSource(sunflowerApi)
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    /**
     * Fetch a list of [Plant]s from the database.
     * Returns a LiveData-wrapped List of Plants.
     */
    val plantsFlow: Flow<List<Plant>>
        get() = plantDao.getPlantsFlow()
            .combine(customSortFlow) { plantsFlow, sortFlow ->
                plantsFlow.applySort(sortFlow)
            }
            .flowOn(defaultDispatcher)
            .conflate()

    override fun getPlants(): Flow<List<Plant>> {
        return plantDao.getPlantsFlow()
            .combine(customSortFlow) { plantsFlow, sortFlow ->
                plantsFlow.applySort(sortFlow)
            }
            .flowOn(defaultDispatcher)
            .conflate()
    }

    /**
     * Fetch a list of [Plant]s from the database that matches a given [GrowZone].
     * Returns a LiveData-wrapped List of Plants.
     */
    override fun getPlansWithGrowZoneFlow(growZone: GrowZone) =
        plantDao.getPlantsWithGrowZoneNumberFlow(growZone.number)
            .map {
                it.applyMainSafeSort(plantsListSortOrderCache.getOrAwait())
            }

    /**
     * Returns true if we should make a network request.
     */
    private fun shouldUpdatePlantsCache(): Boolean {
        // suspending function, so you can e.g. check the status of the database here
        return true
    }

    /**
     * Update the plants cache.
     *
     * This function may decide to avoid making a network requests on every call based on a
     * cache-invalidation policy.
     */
    override suspend fun tryUpdateRecentPlantsCache() {
        if (shouldUpdatePlantsCache()) fetchRecentPlants()
    }

    /**
     * Update the plants cache for a specific grow zone.
     *
     * This function may decide to avoid making a network requests on every call based on a
     * cache-invalidation policy.
     */
    override suspend fun tryUpdateRecentPlantsForGrowZoneCache(growZoneNumber: GrowZone) {
        if (shouldUpdatePlantsCache()) fetchPlantsForGrowZone(growZoneNumber)
    }

    /**
     * Fetch a new list of plants from the network, and append them to [plantDao]
     */
    private suspend fun fetchRecentPlants() {
        val plants = plantDataSource.allPlants()
        withContext(defaultDispatcher) {
            plantDao.insertAll(plants)
        }
    }

    /**
     * Fetch a list of plants for a grow zone from the network, and append them to [plantDao]
     */
    private suspend fun fetchPlantsForGrowZone(growZone: GrowZone) {
        val plants = plantDataSource.plantsByGrowZone(growZone)
        withContext(defaultDispatcher) {
            plantDao.insertAll(plants)
        }
    }

    private var plantsListSortOrderCache =
        CacheOnSuccess(onErrorFallback = { emptyList() }) {
            plantDataSource.customPlantSortOrder()
        }

    private fun List<Plant>.applySort(customSortOrder: List<String>): List<Plant> {
        return sortedBy { plant ->
            val positionForItem = customSortOrder.indexOf(plant.plantId).let { order ->
                if (order > -1) order else Int.MAX_VALUE
            }
            ComparablePair(positionForItem, plant.name)
        }
    }

    private val customSortFlow = flow { emit(plantsListSortOrderCache.getOrAwait()) }

    @AnyThread
    suspend fun List<Plant>.applyMainSafeSort(customSortOrder: List<String>) =
        withContext(defaultDispatcher) {
            this@applyMainSafeSort.applySort(customSortOrder)
        }

}