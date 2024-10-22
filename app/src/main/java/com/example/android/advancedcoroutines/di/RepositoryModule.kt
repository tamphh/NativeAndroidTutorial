package com.example.android.advancedcoroutines.di

import com.example.android.advancedcoroutines.data.repository.PlantRepositoryImpl
import com.example.android.advancedcoroutines.domain.repository.IPlantRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlanRepository(impl: PlantRepositoryImpl): IPlantRepository
}