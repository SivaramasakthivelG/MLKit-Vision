package com.example.mlkitvision.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mlkitvision.BaseApplication
import com.example.mlkitvision.data.db.Register
import com.example.mlkitvision.data.db.RegisterWithBitmaps
import com.example.mlkitvision.data.model.FaceAnalyzer
import com.example.mlkitvision.data.model.ImageVerification
import com.example.mlkitvision.util.loadBitmapFromFile
import com.example.mlkitvision.util.saveByteArrayToFile
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

    val todoDao = BaseApplication.database.registerDao()

    val registerList = MutableStateFlow<List<RegisterWithBitmaps>>(emptyList())

    val bitmapListFlow = faceAnalyzer.bitmapListFlow

    private val _detectedFaceCount = faceAnalyzer.detectedFaceCount
    val detectedFaceCount = _detectedFaceCount.asStateFlow()

    private val _camMode = MutableStateFlow(false)
    val camMode = _camMode.asStateFlow()

    init {
        viewModelScope.launch {
            val registers = todoDao.getAllEntities()
            val registersWithBitmaps = registers.map { register ->
                val bitmaps = register.filePathList.mapNotNull { filePath ->
                    loadBitmapFromFile(filePath)
                }
                RegisterWithBitmaps(register.id, register.isProcessed, bitmaps)
            }
            registerList.value = registersWithBitmaps
        }
    }

    fun startFaceDetection(imageProxy: ImageProxy) {
        viewModelScope.launch {
            delay(2000)
            faceAnalyzer.analyze(imageProxy)
        }
    }

    fun insert(register: Register){
        viewModelScope.launch {
            todoDao.insertEntity(register)
        }
    }


    fun isFaceDetected(): Boolean {
        return _detectedFaceCount.value > 0
    }


    fun verifyBitmaps(bitmaps: List<Bitmap>): Boolean {
        return imageVerification.verifyFaces(bitmaps)
    }

    fun onCaptureButtonPressed() {
        faceAnalyzer.onCaptureButtonPressed()
    }

    fun clearFaceCount() {
        faceAnalyzer.detectedFaceCount.value = 0
        faceAnalyzer.captureCounter = 0
    }
}
