package com.example.mlkitvision.viewmodel

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mlkitvision.data.FaceDataStore
import com.example.mlkitvision.data.model.FaceAnalyzer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceDetectionViewModel @Inject constructor(
    private val faceAnalyzer: FaceAnalyzer,
) : ViewModel() {

    // State flow to observe face detection status
    private val _detectedFaceCount = faceAnalyzer.detectedFaceCount
    val detectedFaceCount = _detectedFaceCount.asStateFlow()

    // Start face detection
    fun startFaceDetection(imageProxy: ImageProxy) {
        faceAnalyzer.analyze(imageProxy)
    }

    fun isFaceDetected(): Boolean {
        return _detectedFaceCount.value > 0
    }

    // Save the captured face image to the FaceDataStore
    fun saveFaceImage(context: android.content.Context, bitmap: Bitmap) {
        viewModelScope.launch {
            FaceDataStore.saveImage(context, bitmap)
        }
    }
}
