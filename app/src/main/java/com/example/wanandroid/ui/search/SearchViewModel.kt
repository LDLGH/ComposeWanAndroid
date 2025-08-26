package com.example.wanandroid.ui.search

import com.example.wanandroid.common.BaseViewModel
import com.example.wanandroid.common.UiState
import com.example.wanandroid.data.model.ApiResponse
import com.example.wanandroid.data.model.Article
import com.example.wanandroid.data.model.Articles
import com.example.wanandroid.data.model.Hotkey
import com.example.wanandroid.http.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel : BaseViewModel() {

    private val _uiHotkeyState = MutableStateFlow<UiState<List<Hotkey>>>(UiState.Loading)
    val uiHotkeyState: StateFlow<UiState<List<Hotkey>>> = _uiHotkeyState
    private val _uiHotkeyListState = MutableStateFlow<List<Hotkey>>(emptyList())
    val uiHotkeyListState: StateFlow<List<Hotkey>>
        get() = _uiHotkeyListState.asStateFlow()

    private val _uiArticleState = MutableStateFlow<UiState<Articles>>(UiState.Loading)
    val uiArticleState: StateFlow<UiState<Articles>> = _uiArticleState
    val uiArticleStateList: StateFlow<List<Article>>
        get() = _uiArticleStateList.asStateFlow()
    private val _uiArticleStateList = MutableStateFlow<List<Article>>(emptyList())

    init {
        loadHotkey()
    }

    fun loadHotkey() {
        launchRequest(_uiHotkeyState) {
            val response = RetrofitClient.api.getHotkey()
            if (response.errorCode != 0) {
                // 请求失败
                return@launchRequest ApiResponse(
                    errorCode = response.errorCode,
                    errorMsg = response.errorMsg
                )
            }
            _uiHotkeyListState.value = response.data.orEmpty()
            ApiResponse(
                errorMsg = response.errorMsg,
                errorCode = 0,
                data = response.data
            )
        }
    }

    fun searchArticles(key: String) {
        launchRequest(_uiArticleState) {
            val response = RetrofitClient.api.searchArticles(key)
            if (response.errorCode != 0) {
                // 请求失败
                return@launchRequest ApiResponse(
                    errorCode = response.errorCode,
                    errorMsg = response.errorMsg
                )
            }
            _uiArticleStateList.value = response.data?.datas.orEmpty()
            ApiResponse(
                errorMsg = response.errorMsg,
                errorCode = 0,
                data = response.data
            )
        }
    }

}