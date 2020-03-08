package com.imagedownloader.ui.home

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView.OnEditorActionListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cvapp.base.ApiResponseListener
import com.cvapp.base.BaseFragment
import com.cvapp.base.Status
import com.cvapp.extenstions.isUrlValid
import com.cvapp.extenstions.makeVisible
import com.imagedownloader.R
import com.imagedownloader.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.toast


class HomeFragment : BaseFragment(), ApiResponseListener {

    private var binding: FragmentHomeBinding? = null
    private val viewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }

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
            viewModel.extractImagesFromWeb(binding?.etSearch?.text.toString())
        }

    }

    private fun checkValidation() {
        val webUrl = binding?.etSearch?.text.toString()
        if (isUrlValid(webUrl ?: "")) {
            binding?.webVw?.webViewClient = webViewClient
            binding?.webVw?.loadUrl(webUrl);
        } else {
            context?.toast("Invalid Url")
        }
    }

    override fun <T> onResponse(it: T) {
        if (it is String) {
            context?.toast(it as String)
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

}


