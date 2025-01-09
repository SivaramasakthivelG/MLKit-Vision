package com.example.mlkitvision.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mlkitvision.viewmodel.FaceDetectionViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import java.lang.reflect.Modifier

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    viewModel: FaceDetectionViewModel,
    innerPadding: PaddingValues,
    navController: NavHostController
) {

    val cameraPermissionState =
        rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    PermissionRequired(
        permissionState = cameraPermissionState,
        permissionNotGrantedContent = {
            LaunchedEffect(Unit) {
                cameraPermissionState.launchPermissionRequest()
            }
        },
        permissionNotAvailableContent = {
            Column {
                Text(text = "Camera Permission Denied.")
            }
        }) {
        FaceDetectionScreen(viewModel, innerPadding, navController)
    }
//
//    Column(
//        modifier = androidx.compose.ui.Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Button(onClick = { navController.navigate("faceDetectionScreen") }) {
//            Text("Capture Images")
//        }
//        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
//        Button(onClick = { navController.navigate("listScreen") }) {
//            Text("Show Processed Data")
//        }
//    }

}