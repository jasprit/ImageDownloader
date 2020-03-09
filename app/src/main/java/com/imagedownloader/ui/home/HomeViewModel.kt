package com.imagedownloader.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import coil.Coil
import coil.api.get
import com.cvapp.base.BaseViewModel
import com.cvapp.base.BasicData
import com.cvapp.util.Constants.CSS_QUERY
import com.imagedownloader.model.home.ImageModel
import kotlinx.coroutines.*
import org.jetbrains.anko.toast
import org.jsoup.Jsoup

class HomeViewModel : BaseViewModel<ImageModel>(){

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    fun extractImagesFromWeb(url:String) = coroutineScope.launch {
        val doc = Jsoup.connect(url).get()
        val images = doc.select(CSS_QUERY)

        if (images.isNullOrEmpty()){
            coroutineScope.launch(Dispatchers.Main){
                error.value = Throwable("No Images found in this url please try another one..!")
            }
        }else{
            withContext(Dispatchers.Main){
                msg.value = "Total ${images.count()} Images has been found..!"
            }
            for ((index,value) in images.withIndex()) {
                val src: String = value.absUrl("src")
                Log.d("urlOfImages", " - $src")
                coroutineScope.launch(Dispatchers.Main) {
                    msg.value = "Downloading $index out of ${images.count()} images"
                    data.value = ImageModel(src)
                }
                delay(500)
            }
            coroutineScope.launch(Dispatchers.Main) {
                msg.value = "Downloading Finished"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.coroutineContext.cancelChildren()
    }



}