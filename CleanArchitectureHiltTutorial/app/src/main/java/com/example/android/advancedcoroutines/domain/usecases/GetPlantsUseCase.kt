package com.example.android.advancedcoroutines.domain.usecases

import androidx.annotation.AnyThread
import com.example.android.advancedcoroutines.core.CacheOnSuccess
import com.example.android.advancedcoroutines.core.ComparablePair
import com.example.android.advancedcoroutines.data.local.PlantDao
import com.example.android.advancedcoroutines.data.remote.PlantDataSource
import com.example.android.advancedcoroutines.data.remote.SunflowerApi
import com.example.android.advancedcoroutines.data.repository.PlantRepositoryImpl
import com.example.android.advancedcoroutines.domain.model.GrowZone
import com.example.android.advancedcoroutines.domain.model.NoGrowZone
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

class GetPlantsUseCase @Inject constructor (
    private val plantRepository: IPlantRepository
) {
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    suspend operator fun invoke(growZone: GrowZone): Flow<List<Plant>> {
        return if (growZone == NoGrowZone) {
            plantRepository.getPlants()
                .combine(customSortFlow) { plantsFlow, sortFlow ->
                    plantsFlow.applySort(sortFlow)
                }
                .flowOn(defaultDispatcher)
                .conflate()
        } else {
            plantRepository.getPlansWithGrowZoneFlow(growZone)
                .map {
                    it.applyMainSafeSort(plantsListSortOrderCache.getOrAwait())
                }
        }
    }

    private var plantsListSortOrderCache = plantRepository.getPlantsListSortOrderCache()

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