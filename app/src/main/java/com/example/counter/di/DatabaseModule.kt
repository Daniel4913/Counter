package com.example.counter.di

import android.content.Context
import androidx.room.Room
import com.example.counter.data.database.CounterDatabase
import com.example.counter.util.Constants.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        CounterDatabase::class.java,
        DATABASE_NAME
    )
        .fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun provideOccurrenceDao(database: CounterDatabase) = database.occurrenceDao()


    @Singleton
    @Provides
    fun provideActivityDao(database: CounterDatabase) = database.activityDao()


    @Singleton
    @Provides
    fun provideDescriptionDao(database: CounterDatabase) = database.descriptionDao()

}