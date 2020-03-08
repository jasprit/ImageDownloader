package com.cvapp.extenstions

import android.os.Bundle
import android.view.View
import androidx.navigation.NavArgument
import androidx.navigation.NavOptions
import androidx.navigation.Navigation

fun View.navigateTo(action: Int, bundle: Bundle? = null) {
    Navigation.findNavController(this).navigate(action, bundle)
}

fun View.navigatePopTo(resId: Int, destinationId: Int) {
    val navOptions = NavOptions.Builder().setPopUpTo(destinationId, true).build()
    Navigation.findNavController(this).navigate(resId, null, navOptions)
}

fun View.navigateBack() {
    Navigation.findNavController(this).navigateUp()
}

fun View.getCurrentDestination(): Int? {
    return Navigation.findNavController(this).currentDestination?.id
}


fun View.putValueInGraph(key: String, value: Any?) {
    val graph = Navigation.findNavController(this).graph

    val navArgument = NavArgument.Builder().setDefaultValue(value).build()
    graph.addArgument(key, navArgument)
}

fun View.getValueFromGraph(key: String): String? {
    val graph = Navigation.findNavController(this).graph
    return graph.arguments[key]?.defaultValue?.toString()
}

fun View.getAnyValueFromGraph(key: String): Any? {
    val graph = Navigation.findNavController(this).graph
    return graph.arguments[key]?.defaultValue
}
