package com.example.mlkitvision.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.mlkitvision.data.db.Converters
import com.example.mlkitvision.data.db.Register
import com.example.mlkitvision.util.convertBitmapsToByteArrayList
import com.example.mlkitvision.util.saveByteArrayToFile
import com.example.mlkitvision.viewmodel.FaceDetectionViewModel
import kotlinx.coroutines.launch
import java.sql.Time

@SuppressLint("StateFlowValueCalledInComposition")
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
            .padding(16.dp)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().apply {
                        surfaceProvider = previewView.surfaceProvider
                    }

                    if (camMode.value && !isFaceCaptured.value) {
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setTargetResolution(Size(previewView.width, previewView.height))
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

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                viewModel.onCaptureButtonPressed()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Capture Next Image")
        }

        Button(
            onClick = {
                scope.launch {
                    navController.navigate("listScreen")


                    val byteArrayList = convertBitmapsToByteArrayList(viewModel.bitmapListFlow.value)

                    val filePaths = byteArrayList.mapIndexed { index, byteArray ->
                        val filePath = saveByteArrayToFile(context, byteArray, "image_$index")
                        filePath
                    }
                    val bool = viewModel.verifyBitmaps(viewModel.bitmapListFlow.value)

                    val register = Register(filePathList = filePaths, isProcessed = bool)
                    viewModel.todoDao.insertEntity(register)

                    Toast.makeText(context, "$bool", Toast.LENGTH_SHORT).show()

//                    isFaceCaptured.value = false
//                    camMode.value = true
//                    viewModel.clearFaceCount()
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




