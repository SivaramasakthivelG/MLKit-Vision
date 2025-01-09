package com.example.mlkitvision.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mlkitvision.viewmodel.FaceDetectionViewModel

@Composable
fun ListScreen(
    viewModel: FaceDetectionViewModel,
    modifier: PaddingValues,
    navController: NavHostController
) {
    val bitmapList by viewModel.bitmapListFlow.collectAsState() // Observe bitmap list

    Column(
        modifier = Modifier
            .padding(modifier)
            .fillMaxSize()
    ) {
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(bitmapList) { bitmap ->
                BitmapItem(bitmap = bitmap)
                Spacer(modifier = Modifier.width(2.dp))
            }
        }
    }
}

@Composable
fun BitmapItem(bitmap: Bitmap) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Detected Face",
        modifier = Modifier
            .width(120.dp)
            .padding(8.dp)
            .height(80.dp)
            .rotate(270f),
        contentScale = ContentScale.Crop
    )
}