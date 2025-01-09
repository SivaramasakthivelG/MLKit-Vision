package com.example.mlkitvision.data.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity
data class Register(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "filePathList") val filePathList: List<String>,
    @ColumnInfo(name = "isProcessed") val isProcessed: Boolean
)

@Dao
interface RegisterDao{

    @Insert
    suspend fun insertEntity(entity: Register)

    @Query("SELECT * FROM register")
    suspend fun getAllEntities(): List<Register>

    @Query("SELECT * FROM register WHERE id = :id")
    suspend fun getEntityById(id: Int): Register


}
