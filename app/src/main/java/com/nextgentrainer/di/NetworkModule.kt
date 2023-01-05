package com.nextgentrainer.di

import android.content.Context
import com.nextgentrainer.kotlin.data.repository.GifRepository
import com.nextgentrainer.kotlin.data.repository.MovementRepository
import com.nextgentrainer.kotlin.data.repository.RepetitionRepository
import com.nextgentrainer.kotlin.data.repository.WorkoutRepository
import com.nextgentrainer.kotlin.data.source.CloudStorageSource
import com.nextgentrainer.kotlin.data.source.RepetitionFirebaseSource
import com.nextgentrainer.kotlin.data.source.WorkoutSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.chromium.net.CronetEngine
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideWorkoutRepository(@ApplicationContext context: Context): WorkoutRepository {
        return WorkoutRepository(WorkoutSource(context))
    }

    @Singleton
    @Provides
    fun provideCronetEngine(@ApplicationContext context: Context): CronetEngine {
        return createDefaultCronetEngine(context)
    }

    @Singleton
    @Provides
    fun provideGifRepository(@ApplicationContext context: Context): GifRepository {
        return GifRepository(CloudStorageSource(context))
    }

    @Singleton
    @Provides
    fun provideRepetitionRepository(gifRepository: GifRepository): RepetitionRepository {
        return RepetitionRepository(RepetitionFirebaseSource(), gifRepository)
    }

    @Singleton
    @Provides
    fun provideMovementRepository(): MovementRepository {
        return MovementRepository()
    }

    private fun createDefaultCronetEngine(context: Context): CronetEngine {
        return CronetEngine.Builder(context) // The storage path must be set first when using a disk cache.
            .setStoragePath(context.filesDir.absolutePath)
            .enableHttpCache(
                CronetEngine.Builder.HTTP_CACHE_DISK_NO_HTTP,
                (100 * 1024).toLong()
            )
            .enableHttp2(true)
            .enableQuic(true) // Brotli support is NOT enabled by default.
            .enableBrotli(true) // One can provide a custom user agent if desired.
            .setUserAgent("CronetSampleApp")
            .build()
    }
}
