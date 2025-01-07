package com.example.mlkitvision.data


import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

//create data store
val Context.dataStore by preferencesDataStore(name = "face_data_store")

object FaceDataStore {

    //key and value
    val user_face_id = intPreferencesKey("user_face_id")

    fun getFaceId(context: Context) = context.dataStore.data.map {
        it[user_face_id]
    }

    suspend fun saveFaceId(context: Context, faceId: Int) {
        context.dataStore.edit {
            it[user_face_id] = faceId
        }
    }
}
