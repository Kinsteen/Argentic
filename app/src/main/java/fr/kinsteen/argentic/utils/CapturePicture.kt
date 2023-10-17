package fr.kinsteen.argentic.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import fr.kinsteen.argentic.data.Photo
import fr.kinsteen.argentic.executor
import fr.kinsteen.argentic.rollsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

fun capture(context: Context, coroutineScope: CoroutineScope, imageCaptureUseCase: ImageCapture, selectedRoll: Int, done: () -> Unit) {
    coroutineScope.launch {
        withContext(Dispatchers.IO) {
            imageCaptureUseCase.takePicture(context.executor,
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onError(error: ImageCaptureException) {
                        Log.e("CameraCapture", "Picture error taking picture")
                    }

                    @OptIn(ExperimentalGetImage::class)
                    override fun onCaptureSuccess(image: ImageProxy) {
                        Log.i("CameraCapture", "Picture taken!")
                        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US)
                            .format(System.currentTimeMillis())
                        CoroutineScope(Dispatchers.IO).launch {
                            context.rollsStore.updateData { currentRolls ->
                                currentRolls.toBuilder().setRoll(
                                    selectedRoll,
                                    currentRolls.getRoll(selectedRoll).toBuilder().addPhoto(
                                        Photo.newBuilder().setName("Argentic-$name.jpg").build()
                                    ).build()
                                ).build()
                            }
                        }
                        val cw = ContextWrapper(context)
                        // path to /data/data/yourapp/app_data/imageDir
                        val directory =
                            cw.getDir("imageDir", Context.MODE_PRIVATE)

                        val mypath = File(directory, "Argentic-$name.jpg")
                        val fos = FileOutputStream(mypath)

                        val buffer =
                            image.image?.planes?.get(0)?.buffer ?: return
                        val bytes = ByteArray(buffer.capacity())
                        buffer.get(bytes)
                        val bitmap =
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        Log.i("CameraCapture", "Rotation: " + image.imageInfo.rotationDegrees)
                        val rotated = rotateImage(
                            bitmap,
                            image.imageInfo.rotationDegrees.toFloat()
                        )

                        sepiaFilter(rotated).compress(Bitmap.CompressFormat.JPEG, 85, fos)
                        image.close()
                        done()
                    }
                })
        }
    }
}