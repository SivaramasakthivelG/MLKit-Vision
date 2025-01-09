package com.example.mlkitvision.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
    val bitmapList by viewModel.bitmapListFlow.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .padding(modifier)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(bitmapList) { bitmap ->
                BitmapItem(bitmap = bitmap)
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
            .fillMaxWidth()
            .padding(8.dp)
            .height(300.dp),
        contentScale = ContentScale.Crop
    )
}