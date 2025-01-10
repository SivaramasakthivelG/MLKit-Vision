package com.example.mlkitvision.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.datastore.preferences.core.Preferences
import com.example.mlkitvision.data.db.Register
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

fun convertBitmapsToByteArrayList(bitmaps: List<Bitmap>): List<ByteArray> {
    return bitmaps.map { bitmap ->
        ByteArrayOutputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.toByteArray()
        }
    }
}

fun saveByteArrayToFile(context: Context, byteArray: ByteArray, fileName: String): String {
    val file = File(context.filesDir, fileName)
    file.writeBytes(byteArray)
    return file.absolutePath
}

fun loadBitmapFromFile(filePath: String): Bitmap? {
    val file = File(filePath)
    if (file.exists()) {
        try {
            val inputStream = FileInputStream(file)
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return null
}


