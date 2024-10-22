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

class UpdatePlantsCacheUseCase @Inject constructor (
    private val plantRepository: IPlantRepository
) {
    suspend operator fun invoke(growZone: GrowZone) {
        return if (growZone == NoGrowZone) {
            plantRepository.tryUpdateRecentPlantsCache()
        } else {
            plantRepository.tryUpdateRecentPlantsForGrowZoneCache(growZone)
        }
    }
}