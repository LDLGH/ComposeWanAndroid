package com.example.wanandroid.ui.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wanandroid.data.model.Article
import com.example.wanandroid.data.model.Category
import com.example.wanandroid.ui.WebViewActivity
import com.example.wanandroid.ui.home.HomeViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreen(modifier: Modifier = Modifier, viewModel: ProjectViewModel = viewModel()) {
    val categoryList by viewModel.uiProjectCategoryList.collectAsState()
    if (categoryList.isEmpty()) {
        return
    }
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { categoryList.size }
    )
    Column(modifier.fillMaxSize()) {
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp
        ) {
            categoryList.forEachIndexed { index, item ->
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
        CategoryListScreen(pagerState, categoryList)
    }
}


@Composable
fun CategoryListScreen(
    pagerState: PagerState,
    category: List<Category>,
    modifier: Modifier = Modifier
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->
        category[page].id?.let {
            ArticlesScreen(it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticlesScreen(
    id: Int,
    modifier: Modifier = Modifier,
    viewModel: ProjectViewModel = viewModel(key = "ProjectViewModel_$id")
) {
    val items by viewModel.uiArticleStateList.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
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
    LaunchedEffect(id) {
        viewModel.refreshArticlesIfNeeded(id)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshBoxScreen(
    isRefreshing: Boolean,
    isLoadingMore: Boolean,
    modifier: Modifier = Modifier,
    items: List<Article>,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    hasMore: Boolean,
    listState: LazyListState
) {

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            items(items = items, key = { it.id ?: System.identityHashCode(it) }) { item ->
                AnimatedVisibility(
                    visible = true,
                    enter = expandVertically(animationSpec = tween(300)) + fadeIn(
                        animationSpec = tween(
                            300
                        )
                    )
                ) {
                    ArticleItem(item)
                }
            }
            // 底部加载状态
            item {
                when {
                    isLoadingMore -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    !hasMore -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("没有更多数据了")
                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(listState, isLoadingMore, hasMore) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisible ->
                val total = listState.layoutInfo.totalItemsCount
                if (lastVisible != null && lastVisible >= total - 1 && !isLoadingMore && hasMore) {
                    onLoadMore()
                }
            }
    }

}

@Composable
fun ArticleItem(
    article: Article,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White)
            .clickable {
                WebViewActivity.start(context, article.title, article.link)
            }
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        AsyncImage(
            model = article.envelopePic,
            contentDescription = "envelopePic",
            modifier = Modifier
                .width(100.dp)
                .height(184.dp)
                .padding(top = 12.dp, bottom = 12.dp)
        )

        Column(
            modifier = Modifier
                .width(0.dp)
                .weight(1f)
                .padding(12.dp)
                .fillMaxHeight()
        ) {
            Text(
                article.title.orEmpty(),
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = article.desc.orEmpty(),
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 3,
                minLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 12.dp)
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = "author")
                Text(
                    modifier = Modifier
                        .width(0.dp)
                        .weight(1f)
                        .padding(start = 4.dp),
                    text = article.author?.takeIf { it.isNotBlank() }
                        ?: article.shareUser?.takeIf { it.isNotBlank() } ?: "未知",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = article.niceDate.orEmpty(),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            val icon =
                if (article.collect == true) Icons.Default.Star else Icons.Default.StarOutline
            Icon(
                icon,
                contentDescription = "收藏",
                modifier = Modifier
                    .clickable {
                        viewModel.toggleCollect(article)
                    }
                    .align(Alignment.End)
            )
        }

    }
}
