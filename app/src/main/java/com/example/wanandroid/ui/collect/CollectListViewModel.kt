package com.example.wanandroid.ui.collect

import com.example.wanandroid.common.BaseViewModel
import com.example.wanandroid.common.UiState
import com.example.wanandroid.data.model.ApiResponse
import com.example.wanandroid.data.model.Article
import com.example.wanandroid.data.model.Articles
import com.example.wanandroid.http.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CollectListViewModel : BaseViewModel() {

    private val _uiArticleState = MutableStateFlow<UiState<Articles>>(UiState.Loading)
    val uiArticleState: StateFlow<UiState<Articles>> = _uiArticleState
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()
    private val _hasMore = MutableStateFlow(false)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private var currentPage = 0
    private var pageCount = Int.MAX_VALUE
    private val articleList = mutableListOf<Article>()

    val uiArticleStateList: StateFlow<List<Article>>
        get() = _uiArticleStateList.asStateFlow()
    private val _uiArticleStateList = MutableStateFlow<List<Article>>(emptyList())
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        currentPage = 0
        loadArticles(currentPage, isRefresh = true)
    }

    fun loadMore() {
        if (currentPage + 1 >= pageCount) return
        loadArticles(currentPage + 1, isRefresh = false)
    }

    private fun loadArticles(page: Int, isRefresh: Boolean) {
        if (isRefresh) {
            _isRefreshing.value = true
        } else {
            _isLoadingMore.value = true
        }
        launchRequest(_uiArticleState) {
            val response = RetrofitClient.api.getCollectList(page)
            if (response.errorCode != 0) {
                // 请求失败
                _errorMessage.value = response.errorMsg
                _isRefreshing.value = false
                _isLoadingMore.value = false
                return@launchRequest ApiResponse(
                    errorCode = response.errorCode,
                    errorMsg = response.errorMsg
                )
            }
            val newArticles = response.data?.datas ?: emptyList()
            pageCount = response.data?.pageCount ?: 0

            if (isRefresh) {
                articleList.clear()
            }
            articleList.addAll(newArticles)
            _uiArticleStateList.value = articleList.toList()
            _isRefreshing.value = false
            _isLoadingMore.value = false
            _hasMore.value = page < pageCount - 1
            ApiResponse(
                errorMsg = response.errorMsg,
                errorCode = 0,
                data = response.data
            )
        }
        currentPage = page
    }

}