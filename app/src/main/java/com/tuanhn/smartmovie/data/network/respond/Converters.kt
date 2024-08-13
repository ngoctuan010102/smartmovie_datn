package com.tuanhn.smartmovie.data.network.respond

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromImages(images: Images): String {
        return Gson().toJson(images)
    }

    @TypeConverter
    fun toImages(json: String): Images {
        return Gson().fromJson(json, Images::class.java)
    }
}