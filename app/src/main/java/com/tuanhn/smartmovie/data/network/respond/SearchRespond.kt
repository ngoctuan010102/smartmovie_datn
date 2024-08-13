package com.tuanhn.smartmovie.data.network.respond


data class SearchRespond(
    val films: List<SearchFilmRespond>
)

data class SearchFilmRespond(
    val film_id: Int,
    val imdb_id: Int,
    val imdb_title_id: String,
    val film_name: String,
    val other_titles: Any?,
    val release_dates: List<ReleaseDate>,
    val timescount: String,
    val duration: String,
    val age_rating: List<AgeRating>,
    val images: Images?,
    val film_trailer: String?,
    val synopsis_long: String,
)