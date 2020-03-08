package com.imagedownloader.ui.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.cvapp.base.BaseViewModel
import com.cvapp.base.BasicData
import kotlinx.coroutines.*
import org.jetbrains.anko.toast
import org.jsoup.Jsoup

class HomeViewModel : BaseViewModel<BasicData>(){


    private val job = SupervisorJob()
    protected val coroutineScope = CoroutineScope(Dispatchers.IO + job)


    fun extractImagesFromWeb(url:String) = coroutineScope.launch {
        val doc = Jsoup.connect(url).get()
        val images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]")

        if (images.isNullOrEmpty()){
            withContext(Dispatchers.Main){
                error.value = Throwable("No Images found in this url please try another one..!")
            }
           // return
        }else{
            withContext(Dispatchers.Main){
                msg.value = "Total ${images.count()} Images has been found..!"
            }
            for (el in images) {
                val src: String = el.absUrl("src")
                Log.d("urlOfImages", " - $src")
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        coroutineScope.coroutineContext.cancelChildren()
    }

}