package com.imagedownloader.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cvapp.base.ApiResponseListener
import com.cvapp.base.BaseFragment
import com.imagedownloader.R
import com.imagedownloader.databinding.FragmentHomeBinding
import org.jetbrains.anko.toast


class HomeFragment : BaseFragment(), ApiResponseListener {

    private var binding: FragmentHomeBinding? = null
    private val viewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCallbacks(viewModel, this)
        initViews()
    }


    private fun initViews() {
        //  binding?.webVw?.settings?.javaScriptEnabled = true
        //  binding?.webVw?.webViewClient = WebViewController
        binding?.webVw?.loadUrl("https://www.google.com");

        viewModel.extractImagesFromWeb()
    }

    override fun <T> onResponse(it: T) {
        context?.toast("sucess")
    }

    override fun onError(it: Throwable) {
        context?.toast(it.message.toString())
    }

}


