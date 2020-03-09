package com.imagedownloader.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.imagedownloader.extenstions.createDialog


abstract class BaseActivity : AppCompatActivity() {

    var mContext: Context? = null
    lateinit var loader: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        loader = createDialog(mContext)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    open fun <T> getCallbacks(
        viewModel: BaseViewModel<T>,
        apiResponseListener: ApiResponseListener? = null,
        showLoader: Boolean = true
    ) {
        viewModel.status().observe(this, Observer {
            when (it) {
                Status.LOADING -> {
                    if (showLoader) loader.show()
                }
                else -> {
                    if (showLoader) loader.dismiss()
                }
            }
        })

        viewModel.error().observe(this, Observer {
            apiResponseListener?.onError(it)
        })

        viewModel.response().observe(this, Observer {
            apiResponseListener?.onResponse(it)
        })
    }
}