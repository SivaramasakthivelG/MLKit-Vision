package com.example.mlkitvision.data.db


import android.graphics.Bitmap
import android.util.Base64
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.util.*

class Converters {

    @TypeConverter
    fun fromFilePathList(filePathList: List<String>?): String {
        return filePathList?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toFilePathList(data: String): List<String> {
        return data.split(",").toList()
    }

}
