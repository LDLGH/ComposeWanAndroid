package com.example.wanandroid.ui.category

import com.example.wanandroid.common.BaseViewModel
import com.example.wanandroid.common.UiState
import com.example.wanandroid.data.model.ApiResponse
import com.example.wanandroid.data.model.Article
import com.example.wanandroid.data.model.Articles
import com.example.wanandroid.data.model.Category
import com.example.wanandroid.http.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryViewModel : BaseViewModel() {
    private val _uiCategoryState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val uiCategoryState: StateFlow<UiState<List<Category>>> = _uiCategoryState
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    private val _uiCategoryListState = MutableStateFlow<List<Category>>(emptyList())
    val uiCategoryListState: StateFlow<List<Category>>
        get() = _uiCategoryListState.asStateFlow()

    private val _uiArticleState = MutableStateFlow<UiState<Articles>>(UiState.Loading)
    val uiArticleState: StateFlow<UiState<Articles>> = _uiArticleState

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
    private val loadedIds = mutableSetOf<Int>()

    fun refreshIfNeeded(id: Int) {
        if (id !in loadedIds) {
            loadedIds.add(id)
            refresh()
        }
    }

    fun refresh() {
        _isRefreshing.value = true
        loadCategory()
    }

    fun loadCategory() {
        launchRequest(_uiCategoryState) {
            val response = RetrofitClient.api.getCategory()
            if (response.errorCode != 0) {
                // 请求失败
                _errorMessage.value = response.errorMsg
                _isRefreshing.value = false
                return@launchRequest ApiResponse(
                    errorCode = response.errorCode,
                    errorMsg = response.errorMsg
                )
            }
            _uiCategoryListState.value = response.data.orEmpty()
            _isRefreshing.value = false
            ApiResponse(
                errorMsg = response.errorMsg,
                errorCode = 0,
                data = response.data
            )
        }
    }

    fun refreshArticlesIfNeeded(id: Int) {
        if (id !in loadedIds) {
            loadedIds.add(id)
            currentPage = 0
            loadArticles(currentPage, id, isRefresh = true)
        }
    }


    fun refreshArticles(id: Int) {
        currentPage = 0
        loadArticles(currentPage, id, isRefresh = true)
    }

    fun loadMoreArticles(id: Int) {
        if (currentPage + 1 >= pageCount) return
        loadArticles(currentPage + 1, id, isRefresh = false)
    }

    fun loadArticles(page: Int, id: Int, isRefresh: Boolean) {
        if (isRefresh) {
            _isRefreshing.value = true
        } else {
            _isLoadingMore.value = true
        }
        launchRequest(_uiArticleState) {
            val response = RetrofitClient.api.getCategoryArticles(page, id)
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
            val newArticles = response.data?.datas.orEmpty()
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