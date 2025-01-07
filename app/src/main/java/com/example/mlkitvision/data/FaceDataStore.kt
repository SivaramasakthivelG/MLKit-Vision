package com.example.mlkitvision.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mlkitvision.util.convertBitmapToByteArray
import com.example.mlkitvision.util.convertByteArrayToBitmap
import com.example.mlkitvision.util.toBase64
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

//create data store
val Context.dataStore by preferencesDataStore(name = "face_data_store")

object FaceDataStore {


    suspend fun saveImage(context: Context, bitmap: Bitmap) {
        val imageId = UUID.randomUUID().toString()
        val imageByteArray = convertBitmapToByteArray(bitmap)

        val imageKey = stringPreferencesKey(imageId)
        context.dataStore.edit { preferences ->
            preferences[imageKey] = imageByteArray.toBase64()
            Log.d("$imageId", "savedImage: ")
        }
    }

    suspend fun getAllImages(context: Context): List<String> {
        val imageList = mutableListOf<String>()

        context.dataStore.data.collect { preferences ->
            imageList.add(preferences.toString())
        }

        return imageList
    }

}
