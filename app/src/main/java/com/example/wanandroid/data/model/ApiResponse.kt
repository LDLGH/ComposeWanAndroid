package com.example.wanandroid.data.model

// 通用接口返回
data class ApiResponse<T>(
    val data: T? = null,
    val errorCode: Int = 0,
    val errorMsg: String = ""
)
