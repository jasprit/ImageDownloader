package com.cvapp.base

data class BasicData(val statusCode: Int, val error: String, val msg: String)

data class BasicResponse<out T>(
    val data: T? = null,
    val msg: String? = null,
    val statusCode: Int? = null
)   