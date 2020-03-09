package com.imagedownloader.ui.home

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import coil.Coil
import coil.api.get
import com.cvapp.base.ApiResponseListener
import com.cvapp.base.BaseFragment
import com.cvapp.base.Status
import com.cvapp.extenstions.isUrlValid
import com.cvapp.extenstions.makeVisible
import com.cvapp.util.Constants
import com.cvapp.util.Constants.PERMISSION_REQUEST_CODE
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.coroutinespermission.PermissionManager
import com.imagedownloader.R
import com.imagedownloader.databinding.FragmentHomeBinding
import com.imagedownloader.model.home.ImageModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class HomeFragment : BaseFragment(), ApiResponseListener {

    private var binding: FragmentHomeBinding? = null
    private val viewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(parentJob + Dispatchers.Default)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCallbacks(viewModel, this)
        initViews()
    }

    private fun initViews() {

        binding?.etSearch?.setOnEditorActionListener(OnEditorActionListener { arg0, arg1, arg2 ->
            if (arg1 == EditorInfo.IME_ACTION_GO) {
                // search pressed and perform your functionality.
                checkValidation()
            }
            false
        })

        binding?.fb?.setOnClickListener {
            checkPermissions()
        }

    }


    private fun checkValidation() {
        val webUrl = binding?.etSearch?.text.toString()
        if (isUrlValid(webUrl ?: "")) {
            binding?.webVw?.webViewClient = webViewClient
            binding?.webVw?.settings?.javaScriptEnabled  = true
            binding?.webVw?.loadUrl(webUrl);
        } else {
            context?.toast("Invalid Url")
        }
    }

    override fun <T> onResponse(it: T) {
        when (it) {
            is String -> {
                context?.toast(it as String)
            }
            is ImageModel -> {
                saveImageToStorage((it as ImageModel).imageUrl ?: "")
            }
        }
    }

    override fun onError(it: Throwable) {
        context?.toast(it.message.toString())
    }


    private val webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            viewModel.status.value = Status.SUCCESS
            fb.makeVisible()
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            viewModel.status.value = Status.LOADING
        }
    }


    private fun checkPermissions() {

        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                handleResult(
                    PermissionManager.requestPermissions(
                        this@HomeFragment, Constants.PERMISSION_REQUEST_CODE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }

    private fun handleResult(permissionResult: PermissionResult) {
        when (permissionResult) {
            is PermissionResult.PermissionGranted -> {
                context?.toast("Granted")
                viewModel.extractImagesFromWeb(binding?.etSearch?.text.toString())
            }
            is PermissionResult.PermissionDenied -> {
                context?.toast("Denied")
            }
            is PermissionResult.ShowRational -> {
                val alertDialog = AlertDialog.Builder(requireContext())
                    .setMessage("We need permission")
                    .setTitle("Rational")
                    .setPositiveButton("OK") { _, _ ->
                        when (permissionResult.requestCode) {
                            PERMISSION_REQUEST_CODE -> {
                                coroutineScope.launch(Dispatchers.Main) {
                                    handleResult(
                                        PermissionManager.requestPermissions(
                                            this@HomeFragment,
                                            PERMISSION_REQUEST_CODE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        )
                                    )
                                }
                            }
                        }

                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }.create()
                alertDialog.show()
            }
            is PermissionResult.PermissionDeniedPermanently -> {
                context?.toast("Denied permanently")
            }
        }
    }

    private fun saveImageToStorage(imgUrl: String) = coroutineScope.launch(Dispatchers.IO) {
        val drawable = Coil.get(imgUrl)
        val bm = (drawable as BitmapDrawable).getBitmap()
        saveImage(bm, "jasp")
    }


    @Throws(IOException::class)
    private fun saveImage(bitmap: Bitmap, name: String) = coroutineScope.launch(Dispatchers.IO) {
        val saved: Boolean
        val fos: OutputStream?
        val folderName = "GLOBAL_RELAY"
        fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver: ContentResolver? = context?.contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/$folderName")
            val imageUri =
                resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            resolver?.openOutputStream(imageUri!!)
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            ).toString() + File.separator + folderName
            val file = File(imagesDir)
            if (!file.exists()) {
                file.mkdir()
            }
            val image = File(imagesDir, "$name.png")
            FileOutputStream(image)
        }
        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos!!.flush()
        fos.close()
    }




    override fun onDestroy() {
        super.onDestroy()
        parentJob.cancel()
    }

}



