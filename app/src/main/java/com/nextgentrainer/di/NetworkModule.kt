package com.nextgentrainer.di

import android.content.Context
import com.nextgentrainer.kotlin.data.repository.WorkoutRepository
import com.nextgentrainer.kotlin.data.source.WorkoutSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideWorkoutRepository(@ApplicationContext context: Context): WorkoutRepository {
        return WorkoutRepository(WorkoutSource(context))
    }
}