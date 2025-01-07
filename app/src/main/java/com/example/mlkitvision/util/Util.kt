package com.example.mlkitvision.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.datastore.preferences.core.Preferences
import java.io.ByteArrayOutputStream

fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

fun convertByteArrayToBitmap(byteArray: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}

fun ByteArray.toBase64(): String {
    return android.util.Base64.encodeToString(this, android.util.Base64.DEFAULT)
}


