package com.example.mlkitvision.ui.screens

sealed class Screens(val route: String) {

    data object HomeScreen: Screens("camScreen")
    data object faceDetectionScreen: Screens("faceDetectionScreen")
    data object DataScreen: Screens("DataScreen")
    data object listScreen: Screens("listScreen")


}