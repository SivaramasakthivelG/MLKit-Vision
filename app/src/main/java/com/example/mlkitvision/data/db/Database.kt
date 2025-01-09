package com.example.mlkitvision.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Register::class], version = 2)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Step 1: Create a new table with the correct schema
                database.execSQL(
                    """
                    CREATE TABLE Register_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        filePathList TEXT NOT NULL DEFAULT '',
                        isProcessed INTEGER NOT NULL DEFAULT 0
                    )
                """
                )

                try {
                    database.execSQL(
                        """
                        INSERT INTO Register_new (id, filePathList, isProcessed)
                        SELECT id, filePathList, isProcessed FROM Register
                    """
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                database.execSQL("DROP TABLE IF EXISTS Register")

                database.execSQL("ALTER TABLE Register_new RENAME TO Register")
            }
        }
    }

    abstract fun registerDao(): RegisterDao
}