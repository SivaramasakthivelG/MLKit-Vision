package com.example.mlkitvision.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import com.example.mlkitvision.data.FaceDataStore
import com.example.mlkitvision.viewmodel.FaceDetectionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.math.log

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FaceDetectionScreen(viewModel: FaceDetectionViewModel, innerPadding: PaddingValues) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var scope = rememberCoroutineScope()

    val images = remember { mutableStateOf<List<String>>(emptyList()) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        // Restrict the AndroidView height with weight
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f), // Allocate available space proportionately
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().apply {
                        setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetResolution(Size(previewView.width, previewView.height))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(executor) { imageProxy ->
                        if (previewView.width > 0 && previewView.height > 0) {
                            viewModel.startFaceDetection(imageProxy)

                            Log.d("Face count", "${viewModel.detectedFaceCount.value} ")

                            if (viewModel.isFaceDetected()) {
                                val bitmap = imageProxyToBitmap(imageProxy)
                                bitmap?.let {
                                    scope.launch {
                                        FaceDataStore.saveImage(context, it)
                                    }
                                }
                            }

                            imageProxy.close()

                        } else {
                            Log.e("FaceDetectionScreen", "PreviewView width or height is 0")
                            imageProxy.close()
                        }
                    }

                    cameraProvider.unbindAll()
                    try {
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_FRONT_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.e("FaceDetectionScreen", "Failed to bind camera: ${e.message}")
                    }
                }, executor)

                previewView
            }
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text("Detected Faces: ${viewModel.detectedFaceCount.value}")


        Button(
            onClick = {
                scope.launch {
                    val savedImages = FaceDataStore.getAllImages(context)
                    Log.d("Image Count", "$savedImages.: ")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Register Face")
        }

    }
}

fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
    val planeProxy = imageProxy.planes[0]
    val buffer = planeProxy.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    val yuvImage = YuvImage(bytes, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

