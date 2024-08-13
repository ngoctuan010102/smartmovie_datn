package com.tuanhn.smartmovie.data.network.respond

data class FilmsComingSoonResponse(
    val films: List<Film>,
    val status: Status
)

data class Film(
    val film_id: Int,
    val imdb_id: Int,
    val imdb_title_id: String,
    val film_name: String,
    val other_titles: Any?,
    val release_dates: List<ReleaseDate>,
    val age_rating: List<AgeRating>,
    val film_trailer: String?,
    val synopsis_long: String,
    val images: Images?
)

data class ReleaseDate(
    val release_date: String,
    val notes: String
)

data class AgeRating(
    val rating: String,
    val age_rating_image: String,
    val age_advisory: String?
)

data class Images(
    val poster: Map<String, ImageDetail>,
    val still: Map<String, ImageDetail>
)

data class ImageDetail(
    val image_orientation: String,
    val region: String?,
    val medium: Image
)

data class Image(
    val film_image: String,
    val width: Int,
    val height: Int
)

data class Status(
    val count: Int,
    val state: String,
    val method: String,
    val message: Any?,
    val request_method: String,
    val version: String,
    val territory: String,
    val device_datetime_sent: String,
    val device_datetime_used: String
)