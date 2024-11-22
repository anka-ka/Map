package ru.netology.map.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.map.dao.MarkerDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMarkerDatabase(@ApplicationContext appContext: Context): MarkerDatabase {
        return Room.databaseBuilder(
            appContext,
            MarkerDatabase::class.java,
            "marker_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMarkerDao(markerDatabase: MarkerDatabase): MarkerDao {
        return markerDatabase.markerDao()
    }
}