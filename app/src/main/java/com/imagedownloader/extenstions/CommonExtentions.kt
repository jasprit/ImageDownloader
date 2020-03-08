package com.cvapp.extenstions


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.*
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cvapp.util.Constants
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Kotlin Extensions for simpler, easier and fun way
 * of launching of Activities
 */


fun isEmailValid(email: CharSequence?): Boolean {

    val pattern: Pattern
    val matcher: Matcher

    val emailPattern =
        "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    pattern = Pattern.compile(emailPattern)
    matcher = pattern.matcher(email)

    return matcher.matches()
}

fun View.displaySnackbar(message: String?) {
    Snackbar.make(this, message!!, Snackbar.LENGTH_SHORT).show()
}

fun Context.loadIconsFromArray(arrayID: Int): Array<Drawable?> {
    val ta = resources.obtainTypedArray(arrayID)
    val icons = arrayOfNulls<Drawable>(ta.length())
    for (i in 0 until ta.length()) {
        val id = ta.getResourceId(i, 0)
        if (id != 0) {
            icons[i] = ContextCompat.getDrawable(this, id)
        }
    }
    ta.recycle()
    return icons
}

fun setDialogAttributes(dialog: Dialog?, height: Int) {
    val window = dialog?.window ?: return
    window.setLayout(CoordinatorLayout.LayoutParams.MATCH_PARENT, height)
    window.setGravity(Gravity.CENTER)
    window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
}

fun ViewGroup.inflateLayout(layoutRes: Int): ViewDataBinding {
    return DataBindingUtil.inflate(LayoutInflater.from(this.context), layoutRes, this, false)
}

fun Context.hideKeyboard() {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    val currentFocus = (this as AppCompatActivity).currentFocus
    imm.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun ImageView.loadImage(url: Any?) {
    Glide.with(context)
        .load(url)
        .apply(RequestOptions.centerCropTransform())
        .into(this)
}





fun hasAllPermissionsGranted(@NonNull grantResults: IntArray): Boolean {
    for (grantResult in grantResults) {
        if (grantResult == PackageManager.PERMISSION_DENIED) {
            return false
        }
    }
    return true
}

fun String.toSpanned(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}


@JvmOverloads
fun Context.alert(
    message: String,
    title: String = "",
    positiveButton: String? = null,
    cancelable: Boolean = true,
    callback: (DialogInterface) -> Unit = {}
): AlertDialog.Builder {
    return AlertDialog.Builder(this).apply {
        if (title.isEmpty().not())
            setTitle(title)
        setMessage(message)
        setPositiveButton(positiveButton ?: getString(android.R.string.ok)) { dialog, _ ->
            callback(
                dialog
            )
        }
        setCancelable(cancelable)
        show()
    }
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

/**
 * Adds TextWatcher to the EditText
 */
fun EditText.onTextChanged(listener: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            listener(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}


fun isMarshmallowOrHigher(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}


fun Context.hasFlash(): Boolean {
    return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
}

fun TextView.setMaxLength(length: Int) {
    this.filters = arrayOf(InputFilter.LengthFilter(10))
}

fun Window.setColorStatus(color: Int) {
    addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    statusBarColor = color
    if (color == Color.WHITE && isMarshmallowOrHigher()) {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
        decorView.systemUiVisibility = View.STATUS_BAR_VISIBLE
    }
}


fun EditText.isEmptyField(): Boolean {
    return this.text.toString().isEmpty()
}

/**
 * Button enabling/disabling modifiers
 */

fun AppCompatButton.disableButton() {
    isEnabled = false
    alpha = 0.7f
}

fun AppCompatButton.enableButton() {
    isEnabled = true
    alpha = 1.0f
}

