package com.tuanhn.smartmovie.data.di

import com.tuanhn.smartmovie.data.model.dao.AgeRatingDao
import com.tuanhn.smartmovie.data.model.dao.FilmDao
import com.tuanhn.smartmovie.data.network.ApiService
import com.tuanhn.smartmovie.data.repository.DatabaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDatabaseRepository(
        @IODispatcher ioDispatcher: CoroutineDispatcher,
        filmDao: FilmDao, ageRatingDao: AgeRatingDao
    ): DatabaseRepository {

        return DatabaseRepository(
            ioDispatcher,
            filmDao, ageRatingDao
        )
    }

}