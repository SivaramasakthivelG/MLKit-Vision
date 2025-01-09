package com.example.mlkitvision

import android.app.Application
import androidx.room.Room
import com.example.mlkitvision.data.db.Database
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication: Application() {


    companion object {
        lateinit var database: Database
        const val NAME = "Vision_DB"
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            Database::class.java,
            NAME)
            .addMigrations(Database.MIGRATION_1_2)
            .build()

    }


}