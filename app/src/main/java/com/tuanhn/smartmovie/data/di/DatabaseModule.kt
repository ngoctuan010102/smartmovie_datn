package com.tuanhn.smartmovie.data.di

import android.content.Context
import androidx.room.Room
import com.tuanhn.smartmovie.data.model.AppDB
import com.tuanhn.smartmovie.data.model.dao.AgeRatingDao
import com.tuanhn.smartmovie.data.model.dao.FilmDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDB {
        return Room.databaseBuilder(
            context,
            AppDB::class.java,
            "smart_movie_hb"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFilmDao(db: AppDB): FilmDao = db.getFilmDao()

    @Provides
    @Singleton
    fun provideAgeRatingDao(db: AppDB): AgeRatingDao = db.getAgeRating()

}