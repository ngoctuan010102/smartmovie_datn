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

    @GET("filmsNowShowing/")
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

   /* @GET("movie/popular")
    suspend fun getPopularMovie(
        @Query("page") query: String,
        @Query("api_key") apiKey: String
    ):
            Response<MovieRespond>

    @GET("movie/upcoming")
    suspend fun getUpComingMovies(
        @Query("page") query: String,
        @Query("api_key") apiKey: String
    ): Response<MovieUpComingRespond>

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovie(
        @Query("page") query: String,
        @Query("api_key") apiKey: String
    ): Response<MovieUpComingRespond>

    //list genre
    @GET("genre/movie/list")
    suspend fun getGenres(@Query("api_key") apiKey: String): Response<GenresRespond>

    //top rated
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") query: String,
        @Query("api_key") apiKey: String
    ): Response<MovieUpComingRespond>

    @GET("search/movie")
    suspend fun getSearchedMovies(
        @Query("query") query: String,
        @Query("api_key") apiKey: String
    ): Response<MovieRespond>

    @GET("movie/{movieId}")
    suspend fun getMovieDetails(
        @Path("movieId") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<MovieDetailsRespond>

    @GET(
        "/3/discover/movie?include_adult=false&include_video=false&language=en-US" +
                "&page=1&sort_by=popularity.desc&api_key=d5b97a6fad46348136d87b78895a0c06"
    )
    suspend fun getMoviesOfGenre(@Query("with_genres") genreId: String): Response<MovieRespond>

    @GET("movie/{movieId}/credits")
    suspend fun getCast(
        @Path("movieId") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<CastRespond>
*/
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