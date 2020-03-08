package com.imagedownloader.ui.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.cvapp.base.BaseViewModel
import com.cvapp.base.BasicData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.jsoup.Jsoup

class HomeViewModel : BaseViewModel<BasicData>(){


    fun extractImagesFromWeb(){

       viewModelScope.launch(Dispatchers.IO) { loadImages() }
    }

    private suspend fun loadImages() {
        val doc = Jsoup.connect("https://android-arsenal.com").get()
        val images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]")

        if (images.isNullOrEmpty()){

            Dispatchers.Main{
                error.value = Throwable("No Images found in this url please try another one..!")
            }
            return
        }

        for (el in images) {
            val src: String = el.absUrl("src")
            Log.d("urlOfImages", " - $src")
        }
    }

}