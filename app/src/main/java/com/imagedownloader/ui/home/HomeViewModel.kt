package com.imagedownloader.ui.home

import android.util.Log
import com.imagedownloader.base.BaseViewModel
import com.imagedownloader.model.home.ImageModel
import com.imagedownloader.util.Constants.CSS_QUERY
import kotlinx.coroutines.*
import org.jsoup.Jsoup

class HomeViewModel : BaseViewModel<Any>() {

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    fun extractImagesFromWeb(url: String) = coroutineScope.launch {
        val doc = Jsoup.connect(url).get()
        val images = doc.select(CSS_QUERY)

        if (images.isNullOrEmpty()) {
            coroutineScope.launch(Dispatchers.Main) {
                error.value = Throwable("No Images found in this url please try another one..!")
            }
        } else {
            for ((index, value) in images.withIndex()) {
                val src: String = value.absUrl("src")
             //   Log.d("urlOfImages", " - $src")
                coroutineScope.launch(Dispatchers.Main) {
                    data.value = "Downloading $index out of ${images.count()} images"
                    data.value = ImageModel(src)
                }
                delay(500)
            }
            coroutineScope.launch(Dispatchers.Main) {
                data.value = "Downloading Finished"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.coroutineContext.cancelChildren()
    }
}