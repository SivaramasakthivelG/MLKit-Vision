package com.example.mlkitvision.data.di

import android.content.Context
import com.example.mlkitvision.data.model.FaceAnalyzer
import com.example.mlkitvision.data.model.ImageVerification
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object VisionModule {

    @Singleton
    @Provides
    fun faceDetector(): FaceDetector {
        val realTimeOpts = FaceDetectorOptions.Builder()
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(0.20f)
            .enableTracking()
            .build()

        return com.google.mlkit.vision.face.FaceDetection.getClient(realTimeOpts)
    }

    @Provides
    fun provideFaceAnalyzer(faceDetector: FaceDetector,@ApplicationContext context: Context): FaceAnalyzer {
        return FaceAnalyzer(faceDetector,context)
    }

    @Provides
    @Singleton
    fun provideInterpreter(@ApplicationContext context: Context): Interpreter {
        val options = Interpreter.Options().apply {
            numThreads = 4 // Optimize for multi-threading
        }
        val modelBuffer = loadModelFile(context, "mobilefacenet.tflite")
        return Interpreter(modelBuffer, options)
    }

    private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    @Provides
    @Singleton
    fun provideImageVerification(interpreter: Interpreter): ImageVerification {
        return ImageVerification(interpreter)
    }



}