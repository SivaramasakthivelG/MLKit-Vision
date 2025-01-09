package com.example.mlkitvision.viewmodel

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mlkitvision.data.FaceDataStore
import com.example.mlkitvision.data.model.FaceAnalyzer
import com.example.mlkitvision.data.model.ImageVerification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FaceDetectionViewModel @Inject constructor(
    private val faceAnalyzer: FaceAnalyzer,
    private val imageVerification: ImageVerification
) : ViewModel() {

    val bitmapListFlow = faceAnalyzer.bitmapListFlow

    private val _detectedFaceCount = faceAnalyzer.detectedFaceCount
    val detectedFaceCount = _detectedFaceCount.asStateFlow()

    private val _camMode = MutableStateFlow(false)
    val camMode = _camMode.asStateFlow()

    fun startFaceDetection(imageProxy: ImageProxy) {
        viewModelScope.launch {
            delay(4000)
            faceAnalyzer.analyze(imageProxy)
        }
    }

    fun isFaceDetected(): Boolean {
        return _detectedFaceCount.value > 0
    }

    fun saveFaceImage(context: android.content.Context, bitmap: Bitmap) {
        viewModelScope.launch {
            FaceDataStore.saveImage(context, bitmap)
        }
    }

    fun verifyBitmaps(bitmaps: List<Bitmap>): Boolean {
        return imageVerification.verifyFaces(bitmaps)
    }
}
