package fr.kinsteen.argentic.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import java.io.IOException


@Throws(IOException::class)
fun rotateImage(bitmap: Bitmap, rotation: Float): Bitmap {
    val matrix = Matrix()
    return if (rotation != 0f) {
        matrix.preRotate(rotation)
        Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width,
            bitmap.height, matrix, true
        )
    } else {
        bitmap
    }
}

fun scaleBitmap(bitmap: Bitmap, scale: Float): Bitmap {
    return Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), false)
}

fun sepiaFilter(bitmap: Bitmap): Bitmap {
    val resultBitmap: Bitmap = bitmap.copy(bitmap.config, true)
    val paint = Paint()
    val filter: ColorFilter = ColorMatrixColorFilter(floatArrayOf(
        .393f, .769f, .189f, 0f, 0f,
        .349f, .686f, .168f, 0f, 0f,
        .272f, .534f, .131f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    ))
    paint.colorFilter = filter
    val canvas = Canvas(resultBitmap)
    canvas.drawBitmap(resultBitmap, 0f, 0f, paint)
    return resultBitmap
}