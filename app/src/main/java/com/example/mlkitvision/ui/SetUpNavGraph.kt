package com.example.mlkitvision.ui


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mlkitvision.ui.screens.CameraScreen
import com.example.mlkitvision.ui.screens.ListScreen
import com.example.mlkitvision.ui.screens.Screens
import com.example.mlkitvision.viewmodel.FaceDetectionViewModel

@Composable
fun SetUpNavGraph(
    navController: NavHostController,
    viewModel: FaceDetectionViewModel,
    modifier: PaddingValues
) {

    NavHost(
        navController = navController,
        startDestination = Screens.cameraScreen.route
    ) {

        composable(route = Screens.cameraScreen.route) {
            CameraScreen(viewModel, modifier,navController)
        }
        composable(route = Screens.listScreen.route) {
            ListScreen(viewModel,modifier,navController)
        }
    }

}