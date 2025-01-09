package com.example.mlkitvision

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.test.core.app.ApplicationProvider
import com.example.mlkitvision.data.FaceDataStore
import com.example.mlkitvision.data.model.FaceAnalyzer
import com.example.mlkitvision.util.convertBitmapToByteArray
import com.google.mlkit.vision.face.FaceDetector
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class Utils {
    private lateinit var bitmap: Bitmap
    private lateinit var context: Context
    private lateinit var faceAnalyzer: FaceAnalyzer

    @Before
    fun setup() {
        bitmap = Mockito.mock(Bitmap::class.java)
        context = ApplicationProvider.getApplicationContext()
        faceAnalyzer = FaceAnalyzer(Mockito.mock(FaceDetector::class.java), context)
    }

    @Test
    fun test1() {
        convertBitmapToByteArray(bitmap)
    }

    @Test
    fun FaceDataStoreTest() = runTest {
        FaceDataStore.saveImage(context, bitmap)
        FaceDataStore.getAllImages(context)
        FaceDataStore.deleteAllImages(context)
    }

    @Test
    fun testFaceAnalyzer() {
        faceAnalyzer.analyze(Mockito.mock(ImageProxy::class.java))
    }
}
