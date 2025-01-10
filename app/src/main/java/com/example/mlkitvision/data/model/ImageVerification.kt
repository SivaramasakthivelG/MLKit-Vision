package com.example.mlkitvision.data.model

import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class ImageVerification @Inject constructor(
    private val interpreter: Interpreter
) {

    companion object {
        private const val INPUT_SIZE = 112
        private const val OUTPUT_SIZE = 192
        private const val THRESHOLD = 80
        private const val LOW_THRESHOLD = 20
    }

    private fun preprocessBitmap(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
        val buffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * 3)
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        resizedBitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        for (pixel in pixels) {
            buffer.putFloat(((pixel shr 16 and 0xFF) - 127.5f) / 128f) // Red
            buffer.putFloat(((pixel shr 8 and 0xFF) - 127.5f) / 128f)  // Green
            buffer.putFloat(((pixel and 0xFF) - 127.5f) / 128f)        // Blue
        }
        return buffer
    }

    private fun getEmbedding(bitmap: Bitmap): FloatArray {
        val inputBuffer = preprocessBitmap(bitmap)
        val outputBuffer = Array(1) { FloatArray(OUTPUT_SIZE) }

        interpreter.run(inputBuffer, outputBuffer)
        return outputBuffer[0]
    }

    private fun calculateCosineSimilarity(embedding1: FloatArray, embedding2: FloatArray): Float {
        val dotProduct = embedding1.zip(embedding2).map { (e1, e2) -> e1 * e2 }.sum()
        val norm1 = Math.sqrt(embedding1.map { it * it }.sum().toDouble())
        val norm2 = Math.sqrt(embedding2.map { it * it }.sum().toDouble())
        return ((dotProduct / (norm1 * norm2)) * 100).toFloat()
    }


    fun verifyFaces(bitmaps: List<Bitmap>): Boolean {
        if (bitmaps.size < 3) throw IllegalArgumentException("Bitmap list must contain at least 3 images")

        val embeddings = bitmaps.map { getEmbedding(it) }

        for (i in embeddings.indices) {
            for (j in i + 1 until embeddings.size) {
                val similarity = calculateCosineSimilarity(embeddings[i], embeddings[j])
                if (similarity > THRESHOLD || similarity < LOW_THRESHOLD) {
                    return false
                }
            }
        }
        return true
    }
}
