package com.example.mlkitvision

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.Image
import android.os.Looper
import androidx.camera.core.ImageInfo
import androidx.camera.core.ImageProxy
import androidx.test.core.app.ApplicationProvider
import com.example.mlkitvision.data.model.FaceAnalyzer
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.common.MlKit
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.nio.ByteBuffer


@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class FaceAnalyzerTest {

    private lateinit var context: Context
    private lateinit var faceAnalyzer: FaceAnalyzer
    private lateinit var faceDetector: FaceDetector
    private lateinit var imageProxy: ImageProxy
    private lateinit var mediaImage: Image
    private lateinit var bitmap: Bitmap

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        MlKit.initialize(context)

        faceDetector = Mockito.mock(FaceDetector::class.java)
        faceAnalyzer = FaceAnalyzer(faceDetector, context)

        imageProxy = Mockito.mock(ImageProxy::class.java)
        mediaImage = Mockito.mock(Image::class.java)
        bitmap = Mockito.mock(Bitmap::class.java)

        Mockito.`when`(mediaImage.format).thenReturn(ImageFormat.YUV_420_888)

        val planeMock = Mockito.mock(Image.Plane::class.java)
        val bufferMock = Mockito.mock(ByteBuffer::class.java)


        Mockito.`when`(planeMock.getBuffer()).thenReturn(bufferMock)


        val planeArray = arrayOf(planeMock)
        Mockito.`when`(mediaImage.planes).thenReturn(planeArray)


        Mockito.`when`(mediaImage.height).thenReturn(640)
        Mockito.`when`(mediaImage.width).thenReturn(480)

        Mockito.`when`(imageProxy.imageInfo).thenReturn(Mockito.mock(ImageInfo::class.java))
        Mockito.`when`(imageProxy.height).thenReturn(100)
        Mockito.`when`(imageProxy.width).thenReturn(100)
        Mockito.`when`(imageProxy.imageInfo.rotationDegrees).thenReturn(0)
        Mockito.`when`(imageProxy.image).thenReturn(mediaImage)


        val planevalue = Mockito.mock(ImageProxy.PlaneProxy::class.java)
        Mockito.`when`(planevalue.buffer).thenReturn(bufferMock)

        val list = arrayOf(planevalue, planevalue, planevalue)
        Mockito.`when`(imageProxy.planes).thenReturn(list)


        Mockito.doNothing().`when`(imageProxy).close()
    }

    @Test
    fun testanalyzeno_face_detected()= runTest{
        val face = Mockito.mock(Face::class.java)
        Mockito.`when`(faceDetector.process(Mockito.any(InputImage::class.java)))
            .thenReturn(Tasks.forResult(listOf(face)))

        faceAnalyzer.analyze(imageProxy)

        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        Mockito.`when`(faceDetector.process(Mockito.any(InputImage::class.java)))
            .thenReturn(Tasks.forResult(emptyList()))


        faceAnalyzer.analyze(imageProxy)


    }


}
