package com.example.wanandroid.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanandroid.data.model.ApiResponse
import com.example.wanandroid.http.apiRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {

    // 通用方法：发起请求并返回 UiState
    protected fun <T> launchRequest(
        stateFlow: MutableStateFlow<UiState<T>>,
        requestBlock: suspend () -> ApiResponse<T>
    ) {
        viewModelScope.launch {
            stateFlow.value = UiState.Loading
            when (val result = apiRequest { requestBlock() }) {
                is ApiResult.Success -> stateFlow.value = UiState.Success(result.data)
                is ApiResult.Failure -> stateFlow.value = UiState.Error(result.message)
                is ApiResult.Exception -> stateFlow.value =
                    UiState.Error(result.throwable.localizedMessage ?: "未知错误")
            }
        }
    }
}
