package com.example.android.advancedcoroutines.data.remote

import com.example.android.advancedcoroutines.domain.model.Plant
import retrofit2.http.GET

interface SunflowerApi {
    @GET("googlecodelabs/kotlin-coroutines/master/advanced-coroutines-codelab/sunflower/src/main/assets/plants.json")
    suspend fun getAllPlants() : List<Plant>

    @GET("googlecodelabs/kotlin-coroutines/master/advanced-coroutines-codelab/sunflower/src/main/assets/custom_plant_sort_order.json")
    suspend fun getCustomPlantSortOrder() : List<Plant>
}