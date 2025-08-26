package com.example.wanandroid.ui.category

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanandroid.data.model.Category
import com.example.wanandroid.ui.home.PullToRefreshBoxScreen
import com.example.wanandroid.ui.theme.WanAndroidTheme
import kotlinx.coroutines.launch

class CategoryListActivity : ComponentActivity() {

    companion object {
        const val KEY_CATEGORY = "key_category"

        @JvmStatic
        fun start(context: Context, category: Category) {
            val starter = Intent(context, CategoryListActivity::class.java)
                .putExtra(KEY_CATEGORY, category)
            context.startActivity(starter)
        }
    }

    @Suppress("DEPRECATION")
    private val category by lazy { intent.getSerializableExtra(KEY_CATEGORY) as Category }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WanAndroidTheme {
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount = { category.children.orEmpty().size }
                )
                val scope = rememberCoroutineScope()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            colors = topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text(
                                    category.name.orEmpty(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    finish()
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = {

                                }) {
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                }
                            }
                        )
                    },
                ) { innerPadding ->
                    Column(Modifier.padding(innerPadding)) {
                        PrimaryScrollableTabRow(
                            selectedTabIndex = pagerState.currentPage,
                            edgePadding = 0.dp
                        ) {
                            category.children?.forEachIndexed { index, item ->
                                Tab(
                                    selected = pagerState.currentPage == index,
                                    onClick = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    },
                                    text = {
                                        Text(
                                            text = item.name.orEmpty(),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                )
                            }
                        }
                        CategoryListScreen(pagerState, category)
                    }
                }
            }
        }
    }

}

@Composable
fun CategoryListScreen(
    pagerState: PagerState,
    category: Category,
    modifier: Modifier = Modifier
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->
        category.children?.get(page)?.id?.let {
            ArticlesScreen(it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticlesScreen(
    id: Int,
    modifier: Modifier = Modifier,
    viewModel: CategoryViewModel = viewModel(key = "CategoryViewModel_$id")
) {
    val items by viewModel.uiArticleStateList.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val errorMsg by viewModel.errorMessage.collectAsState()

    val listState = rememberLazyListState()
    PullToRefreshBoxScreen(
        modifier = modifier,
        isRefreshing = isRefreshing,
        isLoadingMore = isLoadingMore,
        items = items,
        onRefresh = {
            viewModel.refreshArticles(id)
        },
        onLoadMore = {
            viewModel.loadMoreArticles(id)
        },
        hasMore = hasMore,
        listState = listState
    )
    val context = LocalContext.current
    LaunchedEffect(errorMsg) {
        errorMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(id) {
        viewModel.refreshArticlesIfNeeded(id)
    }
}
