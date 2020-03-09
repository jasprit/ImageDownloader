package com.imagedownloader.ui.home

import android.Manifest
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import coil.Coil
import coil.api.get
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.coroutinespermission.PermissionManager
import com.imagedownloader.R
import com.imagedownloader.base.ApiResponseListener
import com.imagedownloader.base.BaseFragment
import com.imagedownloader.base.Status
import com.imagedownloader.databinding.FragmentHomeBinding
import com.imagedownloader.extenstions.displaySnackbar
import com.imagedownloader.extenstions.isUrlValid
import com.imagedownloader.extenstions.makeVisible
import com.imagedownloader.extenstions.saveImage
import com.imagedownloader.model.home.ImageModel
import com.imagedownloader.util.Constants
import com.imagedownloader.util.Constants.PERMISSION_REQUEST_CODE
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import org.jetbrains.anko.toast


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

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState?.isEmpty == false) {
            binding?.webVw?.restoreState(savedInstanceState)
            binding?.fb?.makeVisible()
        }
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

    /**
     * After valid http url app preview it further
     */
    private fun checkValidation() {
        var webUrl = binding?.etSearch?.text.toString()

        if (!isUrlValid(webUrl ?: "")) {
            webUrl = "http://$webUrl.com"
            binding?.etSearch?.setText(webUrl)
        }

        binding?.webVw?.loadUrl(webUrl);
        binding?.webVw?.settings?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        binding?.webVw?.settings?.javaScriptEnabled = true
        binding?.webVw?.settings?.domStorageEnabled = true
        binding?.webVw?.settings?.loadWithOverviewMode = true
        binding?.webVw?.webViewClient = webViewClient

    }

    /**
     * ViewModel observers here...
     */
    override fun <T> onResponse(it: T) {
        when (it) {
            is String -> {
                //  context?.toast(it as String)
                binding?.root?.displaySnackbar(it as String)
            }
            is ImageModel -> {
                saveImageToStorage((it as ImageModel).imageUrl ?: "")
            }
        }
    }

    override fun onError(it: Throwable) {
        context?.toast(it.message.toString())
    }

    /**
     * webView Client used for in-app web preview
     */

    private val webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            viewModel.status.value = Status.SUCCESS
            binding?.etSearch?.setText(url)
            fb.makeVisible()
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            viewModel.status.value = Status.LOADING
       //     binding?.etSearch?.setText(url)
        }
    }


    /**
     * This functioned can be moved to common extensions as well
     * permission check here with coroutines
     */
    private fun checkPermissions() {
        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                handlePermissionsResult(
                    PermissionManager.requestPermissions(
                        this@HomeFragment, Constants.PERMISSION_REQUEST_CODE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }

    /**
     * This functioned can be moved to common extensions as well
     * this is for handling run-time permissions
     */
    private fun handlePermissionsResult(permissionResult: PermissionResult) {
        when (permissionResult) {
            is PermissionResult.PermissionGranted -> {
                //   context?.toast(getString(R.string.granted))
                viewModel.extractImagesFromWeb(binding?.etSearch?.text.toString())
            }
            is PermissionResult.PermissionDenied -> {
                context?.toast(getString(R.string.denied))
            }
            is PermissionResult.ShowRational -> {
                showDialog(permissionResult)
            }
            is PermissionResult.PermissionDeniedPermanently -> {
                context?.toast(getString(R.string.denied_permanently))
            }
        }
    }

    /**
     * Writing image to the external device with the help of
     * coil and coroutines
     */

    private fun saveImageToStorage(imgUrl: String) = coroutineScope.launch(Dispatchers.IO) {
        val drawable =
            Coil.get(imgUrl)   // coil used suspend function here to download image here..
        val bm = (drawable as BitmapDrawable).bitmap
        context?.saveImage(bm, coroutineScope)
    }

    private fun showDialog(permissionResult: PermissionResult) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.we_need_permission))
            .setTitle(getString(R.string.rational))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                when (permissionResult.requestCode) {
                    PERMISSION_REQUEST_CODE -> {
                        coroutineScope.launch(Dispatchers.Main) {
                            handlePermissionsResult(
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
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }.create()
        alertDialog.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding?.webVw?.saveState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        parentJob.cancel()
    }

}



