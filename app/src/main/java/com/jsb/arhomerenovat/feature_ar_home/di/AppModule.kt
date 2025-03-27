package com.jsb.arhomerenovat.feature_ar_home.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelDatabase
import com.jsb.arhomerenovat.feature_ar_home.data.repository.ModelRepositoryImpl
import com.jsb.arhomerenovat.feature_ar_home.domain.repository.ModelRepository
import com.jsb.arhomerenovat.feature_ar_home.presentation.ARDepthEstimationViewModel
import com.jsb.arhomerenovat.feature_depth_estimation.data.FrameAnalyser
import com.jsb.arhomerenovat.feature_depth_estimation.data.MiDASModel
import com.jsb.arhomerenovat.presentation.PointCloudGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideModelDatabase(app: Application): ModelDatabase {
        return Room.databaseBuilder(
            app,
            ModelDatabase::class.java,
            ModelDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideModelRepository(db: ModelDatabase): ModelRepository {
        return ModelRepositoryImpl(db.layoutDao(), db.modelDao())
    }

    @Provides
    @Singleton
    fun provideMiDASModel(@ApplicationContext context: Context): MiDASModel {
        return MiDASModel(context)
    }

    @Provides
    @Singleton
    fun providePointCloudGenerator(): PointCloudGenerator {
        return PointCloudGenerator(
            fx = 1000f,
            fy = 1000f,
            cx = 500f,
            cy = 500f
        )
    }
}
