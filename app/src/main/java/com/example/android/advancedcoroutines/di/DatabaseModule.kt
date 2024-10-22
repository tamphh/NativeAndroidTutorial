package com.example.android.advancedcoroutines.di

import android.content.Context
import androidx.room.Room
import com.example.android.advancedcoroutines.data.local.AppDatabase
import com.example.android.advancedcoroutines.data.local.PlantDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun providePlantDao(appDatabase: AppDatabase): PlantDao = appDatabase.plantDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return Room
            .databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "sunflower-db"
            )
            .build()
    }
}