package com.cvapp.extenstions

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.cvapp.util.Constants
import com.cvapp.util.Constants.EXTERNAL_PATH
import com.cvapp.util.Constants.FOLDER_NAME
import com.cvapp.util.Constants.IMAGE_EXT
import com.cvapp.util.Constants.IMAGE_TYPE
import com.google.android.material.snackbar.Snackbar
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

fun ViewGroup.inflateLayout(layoutRes: Int): ViewDataBinding {
    return DataBindingUtil.inflate(LayoutInflater.from(this.context), layoutRes, this, false)
}

fun Context.hideKeyboard() {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    val currentFocus = (this as AppCompatActivity).currentFocus
    imm.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
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

fun isMarshmallowOrHigher(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}

fun Context.showToast(msg:String) = Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();

@Throws(IOException::class)
 fun Context.saveImage(bitmap: Bitmap, coroutineScope:CoroutineScope) = coroutineScope.launch(Dispatchers.IO) {
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
    saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    fos?.flush()
    fos?.close()
}




