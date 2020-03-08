package com.cvapp.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel<T> : ViewModel() {

    val compositeDisposable = CompositeDisposable()

    val status = MutableLiveData<Status>()
    val error = MutableLiveData<Throwable>()
    val msg = MutableLiveData<String>()

    fun response(): MutableLiveData<String> {
        return msg
    }

    fun status(): MutableLiveData<Status> {
        return status
    }

    fun error(): MutableLiveData<Throwable> {
        return error
    }

    override fun onCleared() {
        super.onCleared()

        compositeDisposable.dispose()
    }
}