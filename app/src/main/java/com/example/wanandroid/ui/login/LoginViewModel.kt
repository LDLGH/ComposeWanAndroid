package com.example.wanandroid.ui.login

import androidx.lifecycle.viewModelScope
import com.example.wanandroid.app.App
import com.example.wanandroid.common.ApiResult
import com.example.wanandroid.common.BaseViewModel
import com.example.wanandroid.common.UserPreferences
import com.example.wanandroid.data.model.User
import com.example.wanandroid.http.RetrofitClient
import com.example.wanandroid.http.apiRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginViewModel() : BaseViewModel() {

    val userFlow = UserPreferences.getUser(App.instance)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    private val _uiLoginState = MutableStateFlow(false)
    val uiLoginState = _uiLoginState.asStateFlow()

    private val _uiLoginData = MutableStateFlow<User?>(null)
    val uiLoginData = _uiLoginData.asStateFlow()
    private val _uiRegisterState = MutableStateFlow(false)
    val uiRegisterState = _uiRegisterState.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String?>()
    val errorMessage = _errorMessage.asSharedFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiLoginState.value = true
            when (val result = apiRequest { RetrofitClient.api.login(username, password) }) {
                is ApiResult.Success -> _uiLoginData.value = result.data
                is ApiResult.Failure -> _errorMessage.emit(result.message)
                is ApiResult.Exception -> _errorMessage.emit(
                    result.throwable.localizedMessage ?: "未知错误"
                )
            }
            _uiLoginState.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            UserPreferences.clearUser(App.instance)
        }
    }

    fun register(username: String, password: String, repassword: String) {
        viewModelScope.launch {
            _uiRegisterState.value = true
            when (val result =
                apiRequest { RetrofitClient.api.register(username, password, repassword) }) {
                is ApiResult.Success -> _uiLoginData.value = result.data
                is ApiResult.Failure -> _errorMessage.emit(result.message)
                is ApiResult.Exception -> _errorMessage.emit(
                    result.throwable.localizedMessage ?: "未知错误"
                )
            }
            _uiRegisterState.value = false
        }
    }

}