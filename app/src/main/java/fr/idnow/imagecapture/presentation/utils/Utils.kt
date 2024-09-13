package fr.idnow.imagecapture.presentation.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

private fun createContentValues(): ContentValues {
    return ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, createFileName())
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/IdNow/ImageCapture")
        }
    }
}

fun createFileName(): String {
    return SimpleDateFormat(FILENAME_FORMAT, Locale.FRENCH).format(System.currentTimeMillis())
}

fun saveImage(context:Context, bitmap: Bitmap) {
    val contentValues = createContentValues()
    val imageUri =
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    imageUri?.let { uri ->
        context.contentResolver.openOutputStream(uri)?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.flush()
            showToast(context, "Image downloaded to gallery")
        }
    } ?: showToast(context, "Image not downloaded to gallery")

}

fun showToast(context: Context, message: String, toastLength: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, message, toastLength).show()
}

fun createPhotoFile(context: Context): File {
    val name = SimpleDateFormat(FILENAME_FORMAT, Locale.FRENCH).format(System.currentTimeMillis())
    return File(context.externalCacheDirs?.firstOrNull(), "$name.jpg")
}