package com.imagedownloader.extenstions

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import com.google.android.material.snackbar.Snackbar
import com.imagedownloader.util.Constants
import com.imagedownloader.util.Constants.EXTERNAL_PATH
import com.imagedownloader.util.Constants.FOLDER_NAME
import com.imagedownloader.util.Constants.IMAGE_EXT
import com.imagedownloader.util.Constants.IMAGE_TYPE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


/**
 * Kotlin Extensions for simpler, easier and fun way
 * of launching of Activities
 */


fun isUrlValid(url: String) = URLUtil.isValidUrl(url)

fun getCurrentDate() = Date().time.toString()

fun View.displaySnackbar(message: String?) {
    Snackbar.make(this, message!!, Snackbar.LENGTH_SHORT).show()
}

/**
 * Visibility modifiers and check functions
 */

fun View.isVisibile(): Boolean = visibility == View.VISIBLE

fun View.isGone(): Boolean = visibility == View.GONE

fun View.isInvisible(): Boolean = visibility == View.INVISIBLE

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

/**
 * Hides the soft input keyboard from the screen
 */
fun View.hideKeyboard(context: Context?) {
    val inputMethodManager =
        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}

@Throws(IOException::class)
fun Context.saveImage(bitmap: Bitmap, coroutineScope: CoroutineScope) =
    coroutineScope.launch(Dispatchers.IO) {
        val saved: Boolean
        val fos: OutputStream?
        val folderName = FOLDER_NAME
        val name = Constants.IMAGE_NAME + getCurrentDate()

        fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver: ContentResolver? = contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, IMAGE_TYPE)
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "$EXTERNAL_PATH/$folderName")
            val imageUri =
                resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            imageUri?.let { resolver.openOutputStream(it) }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            ).toString() + File.separator + folderName
            val file = File(imagesDir)
            if (!file.exists()) {
                file.mkdir()
            }
            val image = File(imagesDir, "$name$IMAGE_EXT")
            FileOutputStream(image)
        }
        if (fos == null) return@launch
        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
    }




