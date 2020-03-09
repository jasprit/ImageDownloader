package com.imagedownloader.ui

import android.os.Bundle
import com.imagedownloader.base.BaseActivity
import com.imagedownloader.R


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // navigation library has been added to this activity.
    }
}
