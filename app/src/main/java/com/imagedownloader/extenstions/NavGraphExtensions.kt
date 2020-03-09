package com.cvapp.extenstions

import android.view.View
import androidx.navigation.Navigation


fun View.navigateBack() {
    Navigation.findNavController(this).navigateUp()
}

