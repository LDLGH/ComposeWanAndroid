package com.example.wanandroid.http

import com.example.wanandroid.common.ApiResult
import com.example.wanandroid.data.model.ApiResponse

// 请求安全封装
suspend fun <T> apiRequest(block: suspend () -> ApiResponse<T>): ApiResult<T> {
    return try {
        val response = block()
        if (response.errorCode == 0 && response.data != null) {
            ApiResult.Success(response.data)
        } else {
            ApiResult.Failure(response.errorCode, response.errorMsg)
        }
    } catch (e: Throwable) {
        ApiResult.Exception(e)
    }
}