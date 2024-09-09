package com.tuanhn.smartmovie.data.network


import com.tuanhn.smartmovie.data.network.respond.FilmsComingSoonResponse
import com.tuanhn.smartmovie.data.network.respond.SearchRespond
import com.tuanhn.smartmovie.data.network.respond.ShowTimeRespond
import com.tuanhn.smartmovie.data.utils.Constants.BASE_URL
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {

    @GET("filmLiveSearch/")
    suspend fun searchFilm(
        @Header("client") client: String,
        @Header("x-api-key") apiKey: String,
        @Header("authorization") authorization: String,
        @Header("territory") territory: String,
        @Header("api-version") apiVersion: String,
        @Header("geolocation") geolocation: String,
        @Header("device-datetime") deviceDatetime: String,
        @Query("n") n: Int,
        @Query("query") query: String
    ): Response<SearchRespond>

    /*
    https://api-gate2.movieglu.com/filmLiveSearch/?n=100&query=marvel
     */
//https://api-gate2.movieglu.com/filmsComingSoon/?n=10
    @GET("filmsComingSoon/")
    suspend fun getFilmsComingSoon(
        @Header("client") client: String,
        @Header("x-api-key") apiKey: String,
        @Header("authorization") authorization: String,
        @Header("territory") territory: String,
        @Header("api-version") apiVersion: String,
        @Header("geolocation") geolocation: String,
        @Header("device-datetime") deviceDatetime: String,
        @Query("n") n: Int
    ): Response<FilmsComingSoonResponse>
    
    @GET("filmsNowShowing/")
    suspend fun getFilmsNowPlaying(
        @Header("client") client: String,
        @Header("x-api-key") apiKey: String,
        @Header("authorization") authorization: String,
        @Header("territory") territory: String,
        @Header("api-version") apiVersion: String,
        @Header("geolocation") geolocation: String,
        @Header("device-datetime") deviceDatetime: String,
        @Query("n") n: Int
    ): Response<FilmsComingSoonResponse>

    @GET("filmShowTimes/")
    suspend fun getFilmShowTime(
        @Header("client") client: String,
        @Header("x-api-key") apiKey: String,
        @Header("authorization") authorization: String,
        @Header("territory") territory: String,
        @Header("api-version") apiVersion: String,
        @Header("geolocation") geolocation: String,
        @Header("device-datetime") deviceDatetime: String,
        @Query("n") n: Int,
        @Query("film_id") film_id: Int,
        @Query("date") date: String,
    ): Response<ShowTimeRespond>

}

object RetrofitClient {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}