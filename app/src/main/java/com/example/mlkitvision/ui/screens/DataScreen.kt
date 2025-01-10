package com.example.mlkitvision.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mlkitvision.data.db.Register
import com.example.mlkitvision.util.convertBitmapsToByteArrayList
import com.example.mlkitvision.util.saveByteArrayToFile
import com.example.mlkitvision.viewmodel.FaceDetectionViewModel
import kotlinx.coroutines.launch

@Composable
fun DataScreen(
    viewModel: FaceDetectionViewModel,
    modifier: PaddingValues,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(modifier),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                scope.launch {
                    navController.navigate("listScreen")
                }
            },
            modifier = Modifier
                .width(180.dp)
                .height(100.dp)
                .padding(vertical = 8.dp)
        ) {
            Text("LazyList")
        }
        Button(
            onClick = {
                scope.launch {
                    viewModel.clearDb()
                }
            },
            modifier = Modifier
                .width(180.dp)
                .height(100.dp)
                .padding(vertical = 8.dp)
        ) {
            Text("Delete all data")
        }

    }
}