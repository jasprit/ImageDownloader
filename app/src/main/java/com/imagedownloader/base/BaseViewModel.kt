package com.imagedownloader.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel<T> : ViewModel() {


    val status = MutableLiveData<Status>()
    val error = MutableLiveData<Throwable>()
    val msg = MutableLiveData<String>()
    val data = MutableLiveData<T>()

    fun response(): MutableLiveData<T> {
        return data
    }

    fun status(): MutableLiveData<Status> {
        return status
    }

    fun error(): MutableLiveData<Throwable> {
        return error
    }


}