package com.example.mlkitvision.data.di

import android.content.Context
import com.example.mlkitvision.data.model.FaceAnalyzer
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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



}