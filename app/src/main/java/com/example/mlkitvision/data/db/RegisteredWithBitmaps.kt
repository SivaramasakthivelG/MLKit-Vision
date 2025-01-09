package com.example.mlkitvision.data.db

import android.graphics.Bitmap

data class RegisterWithBitmaps(
    val id: Int,
    val isProcessed: Boolean,
    val bitmapList: List<Bitmap>
)