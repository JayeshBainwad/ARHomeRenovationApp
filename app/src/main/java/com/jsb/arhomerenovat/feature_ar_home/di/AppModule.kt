//package com.jsb.arhomerenovat.feature_ar_home.di
//
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//import android.content.Context
//import androidx.room.Room
//import androidx.room.migration.Migration
//import androidx.sqlite.db.SupportSQLiteDatabase
//import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelDao
//import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelDatabase
//import com.jsb.arhomerenovat.feature_ar_home.data.repository.ModelRepositoryImpl
//import com.jsb.arhomerenovat.feature_ar_home.domain.repository.ModelRepository
//import dagger.hilt.android.qualifiers.ApplicationContext
//
//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//
//    @Provides
//    @Singleton
//    fun provideApplicationContext(@ApplicationContext context: Context): Context {
//        return context
//    }
//
//    @Provides
//    @Singleton
//    fun provideModelDatabase(@ApplicationContext context: Context): ModelDatabase {
//        return Room.databaseBuilder(
//            context,
//            ModelDatabase::class.java,
//            "geo_model_database"
//        ).build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideModelDao(database: ModelDatabase): ModelDao {
//        return database.modelDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideModelRepository(dao: ModelDao): ModelRepository {
//        return ModelRepositoryImpl(dao)
//    }
//}


package com.jsb.arhomerenovat.feature_ar_home.di

import android.app.Application
import androidx.room.Room
import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelDatabase
import com.jsb.arhomerenovat.feature_ar_home.data.repository.ModelRepositoryImpl
import com.jsb.arhomerenovat.feature_ar_home.domain.repository.ModelRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideModelRepository(modelDatabase: ModelDatabase): ModelRepository {
        return ModelRepositoryImpl(modelDatabase.modelDao())
    }
}
