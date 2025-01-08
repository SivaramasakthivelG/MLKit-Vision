package com.example.mlkitvision.ui.screens

import android.annotation.SuppressLint
import android.graphics.RectF
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.mlkitvision.viewmodel.FaceDetectionViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.Canvas as Canvas1


@Composable
fun FaceDetectionScreen(
    viewModel: FaceDetectionViewModel,
    innerPadding: PaddingValues,
    navController: NavHostController
) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var camMode = remember { mutableStateOf(true) }
    var isFaceCaptured = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
        ) {
            // Camera Preview
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val executor = ContextCompat.getMainExecutor(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().apply {
                            setSurfaceProvider(previewView.surfaceProvider)
                        }

                        if (camMode.value && !isFaceCaptured.value) {
                            val imageAnalysis = ImageAnalysis.Builder()
                                .setTargetResolution(android.util.Size(previewView.width, previewView.height))
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()

                            imageAnalysis.setAnalyzer(executor) { imageProxy ->
                                if (previewView.width > 0 && previewView.height > 0) {
                                    viewModel.startFaceDetection(imageProxy)

                                    if (viewModel.detectedFaceCount.value >= 3 && !isFaceCaptured.value) {
                                        isFaceCaptured.value = true
                                        camMode.value = false
                                        cameraProvider.unbindAll()
                                        imageProxy.close()
                                        Log.d("FaceDetection", "Face captured, camera closed.")
                                    }

                                    Log.d("Face count", "${viewModel.detectedFaceCount.value}")
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
                        }
                    }, executor)

                    previewView
                }
            )

            Canvas1(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val ovalWidth = canvasWidth * 0.6f  // Full width of the canvas
                val ovalHeight = canvasHeight * 0.4f  // 70% of the height
                val ovalLeft = (canvasWidth - ovalWidth) / 2
                val ovalTop = (canvasHeight - ovalHeight) / 2

                drawRect(
                    color = Color.Black.copy(0.7f),
                    size = size
                )

                drawOval(
                    color = Color.Transparent,
                    topLeft = Offset(ovalLeft, ovalTop),
                    size = Size(ovalWidth, ovalHeight),
                    blendMode = BlendMode.Clear
                )

                drawOval(
                    color = Color.White,
                    topLeft = Offset(ovalLeft, ovalTop),
                    size = Size(ovalWidth, ovalHeight),
                    style = Stroke(width = 4.dp.toPx())
                )
            }

        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                scope.launch {
                    Log.d("Added", "Captured Faces: ${viewModel.detectedFaceCount.value}")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Register Face")
        }

        Button(
            onClick = {
                scope.launch {
                    navController.navigate("listScreen")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("LazyList")
        }
    }
}



