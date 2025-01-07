package com.example.mlkitvision.data.model

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.mlkitvision.data.FaceDataStore
import com.example.mlkitvision.data.FaceDataStore.user_face_id
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FaceAnalyzer @Inject constructor(private val detector: FaceDetector,private val context: Context) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        mediaImage?.let {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        val userFace = faces.firstOrNull() ?: return@addOnSuccessListener

                        if(user_face_id.name.isEmpty()){
                            CoroutineScope(Dispatchers.IO).launch {
                                FaceDataStore.saveFaceId(context, userFace.trackingId!!.toInt())
                                println("Face:  ${userFace.trackingId}")
                            }
                        }else{
                            //verify
                        }

                        Log.d("FaceAnalyzer", "Face Registered: ${userFace.trackingId}")
                    }
                }
                .addOnFailureListener { Log.e("FaceAnalyzer", "Face detection failed") }
                .addOnCompleteListener { imageProxy.close() }
        }
    }
}