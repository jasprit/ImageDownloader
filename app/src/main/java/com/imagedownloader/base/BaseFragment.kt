package com.cvapp.base

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.cvapp.extenstions.createDialog
import com.cvapp.extenstions.navigateBack
import com.cvapp.util.ConnectionLiveData
import com.google.android.material.snackbar.Snackbar
import com.imagedownloader.R
import org.jetbrains.anko.design.indefiniteSnackbar

abstract class BaseFragment : Fragment() {

    lateinit var loader: Dialog
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loader = createDialog(context)

        val connectionLiveData = ConnectionLiveData(context)
        connectionLiveData.observe(this, Observer {
            if (!it && snackbar?.isShown != true) {
                snackbar = view?.indefiniteSnackbar(getString(R.string.error_connection))
            } else {
                snackbar?.dismiss()
            }
        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

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
                    loader.dismiss()
                }
            }
        })

        viewModel.error().observe(this, Observer {
            if (showLoader) apiResponseListener?.onError(it)
        })

        viewModel.response().observe(this, Observer {
            apiResponseListener?.onResponse(it)
        })
    }

    fun onBackPressed() = view?.navigateBack()
}