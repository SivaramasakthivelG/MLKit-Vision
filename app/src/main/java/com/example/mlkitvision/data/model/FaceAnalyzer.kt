package com.example.mlkitvision.data.model

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.mlkitvision.data.FaceDataStore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FaceAnalyzer @Inject constructor(private val detector: FaceDetector,private val context: Context) : ImageAnalysis.Analyzer {

    private val _detectedFaceCount = MutableStateFlow(0)
    val detectedFaceCount = _detectedFaceCount

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        mediaImage?.let {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        val userFace = faces.firstOrNull() ?: return@addOnSuccessListener

                        if(faces.size ==1){
                            detectedFaceCount.value = 1
                        }

                        Log.d("FaceAnalyzer", "Face Registered: ${faces.size}")
                    }else{
                        _detectedFaceCount.value = 0
                    }

                }
                .addOnFailureListener { Log.e("FaceAnalyzer", "Face detection failed") }
                .addOnCompleteListener { imageProxy.close() }
        }
    }
}