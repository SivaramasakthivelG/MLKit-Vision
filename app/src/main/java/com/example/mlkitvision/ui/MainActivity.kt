package com.example.mlkitvision.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.mlkitvision.ui.screens.FaceDetectionScreen
import com.example.mlkitvision.ui.theme.MLKITVisionTheme
import com.example.mlkitvision.viewmodel.FaceDetectionViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MLKITVisionTheme {
                val viewModel = hiltViewModel<FaceDetectionViewModel>()
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) {innerPadding ->
                    SetUpNavGraph(navController = navController,viewModel,innerPadding )
                }
            }
        }
    }
}
