package com.example.mlkitvision.data.model

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject

class FaceAnalyzer @Inject constructor(
    private val detector: FaceDetector,
    private val context: Context
) : ImageAnalysis.Analyzer {

    private val _detectedFaceCount = MutableStateFlow(0)
    val detectedFaceCount = _detectedFaceCount

    private val _bitmapList = MutableStateFlow<List<Bitmap>>(emptyList())
    val bitmapListFlow = _bitmapList.asStateFlow()

    val bitmapList: MutableList<Bitmap> = mutableListOf()

    private var captureCounter = 0
    private val captureInterval = 2000L



    @Override
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        if (_detectedFaceCount.value >= 3 || captureCounter >= 3) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        mediaImage?.let {
            val inputImage =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        val userFace = faces.firstOrNull() ?: return@addOnSuccessListener

                        if (faces.size == 1) {
                            _detectedFaceCount.value = 1

                            val faceBitmap = extractBitmapFromImageProxy(imageProxy)

                            faceBitmap?.let { bitmap ->
                                if (captureCounter < 3) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        delay(captureInterval * captureCounter)
                                        if (captureCounter < 3) {
                                            bitmapList.add(bitmap)
                                            _bitmapList.value = bitmapList
                                            Toast.makeText(
                                                context,
                                                "Image ${captureCounter.plus(1)} Captured",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            captureCounter++
                                            Log.d(
                                                "Image Count",
                                                "Captured Images: ${bitmapList.size}"
                                            )

                                            if (captureCounter >= 3) {
                                                _detectedFaceCount.value = 3
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            _detectedFaceCount.value = 0
                        }
                        Log.d("FaceAnalyzer", "Face Registered: ${faces.size}")
                    } else {
                        _detectedFaceCount.value = 0
                    }
                }
                .addOnFailureListener {
                    Log.e("FaceAnalyzer", "Face detection failed")
                    imageProxy.close()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}


fun extractBitmapFromImageProxy(imageProxy: ImageProxy): Bitmap? {
    val nv21 = yuv420888ToNv21(imageProxy)
    val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
    val outputStream = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, imageProxy.width, imageProxy.height), 100, outputStream)
    val jpegBytes = outputStream.toByteArray()
    return BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
}

fun yuv420888ToNv21(imageProxy: ImageProxy): ByteArray {
    val yBuffer = imageProxy.planes[0].buffer
    val uBuffer = imageProxy.planes[1].buffer
    val vBuffer = imageProxy.planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)

    var uvIndex = ySize
    for (i in 0 until uSize) {
        nv21[uvIndex++] = vBuffer.get(i)
        nv21[uvIndex++] = uBuffer.get(i)
    }

    return nv21
}
