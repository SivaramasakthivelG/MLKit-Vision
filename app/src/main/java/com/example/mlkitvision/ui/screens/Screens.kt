package com.example.mlkitvision.ui.screens

sealed class Screens(val route: String) {

    data object cameraScreen: Screens("camScreen")
//    data object faceDetectionScreen: Screens("faceDetectionScreen")
    data object listScreen: Screens("listScreen")

}