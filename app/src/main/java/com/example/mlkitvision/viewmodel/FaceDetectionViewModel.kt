package com.example.mlkitvision.viewmodel

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import com.example.mlkitvision.data.model.FaceAnalyzer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class FaceDetectionViewModel @Inject constructor(
    private val faceAnalyzer: FaceAnalyzer
) : ViewModel() {

    fun startFaceDetection(imageProxy: ImageProxy) {
        faceAnalyzer.analyze(imageProxy)
    }



}

