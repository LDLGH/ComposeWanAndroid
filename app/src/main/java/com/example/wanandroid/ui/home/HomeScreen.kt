package com.example.wanandroid.ui.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wanandroid.common.UiState
import com.example.wanandroid.data.model.Article
import com.example.wanandroid.data.model.Banner
import com.example.wanandroid.ui.WebViewActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    Column(Modifier.fillMaxSize()) {
        BannerScreen()
        ArticlesScreen()
    }
}

@Composable
fun BannerScreen(modifier: Modifier = Modifier, viewModel: HomeViewModel = viewModel()) {
    val state by viewModel.uiBannerState.collectAsState()
    when (state) {
        is UiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is UiState.Success -> {
            val banners = (state as UiState.Success<List<Banner>>).data
            InfiniteBannerCarousel(
                modifier = modifier,
                imageUrls = banners.map { it.imagePath ?: "" })
        }

        is UiState.Error -> {
            val errorMsg = (state as UiState.Error).message
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "加载失败:$errorMsg ")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        viewModel.loadBanners()
                    }) {
                        Text(text = "重试")
                    }
                }
            }
        }
    }
}

@Composable
fun InfiniteBannerCarousel(
    imageUrls: List<String>,
    modifier: Modifier = Modifier,
    autoScrollInterval: Long = 3000L,
    resumeDelay: Long = 3000L
) {
    if (imageUrls.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    var isAutoScroll by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    // 自动轮播协程
    LaunchedEffect(pagerState) {
        snapshotFlow { isAutoScroll }.collect { auto ->
            while (auto) {
                delay(autoScrollInterval)
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                if (!isAutoScroll) break
            }
        }
    }

    Box(
        modifier = modifier
            .height(200.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // 点击暂停
                        isAutoScroll = false
                        scope.launch {
                            delay(resumeDelay)
                            isAutoScroll = true
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isAutoScroll = false },
                    onDragEnd = {
                        scope.launch {
                            delay(resumeDelay)
                            isAutoScroll = true
                        }
                    }
                ) { _, _ -> }
            }
    ) {
        // 图片轮播
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val index = page % imageUrls.size
            AsyncImage(
                model = imageUrls[index],
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 圆点指示器
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(imageUrls.size) { index ->
                val currentIndex = pagerState.currentPage % imageUrls.size

                val color by animateColorAsState(
                    targetValue = if (currentIndex == index) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary,
                    animationSpec = tween(durationMillis = 300)
                )

                val size by animateDpAsState(
                    targetValue = if (currentIndex == index) 8.dp else 6.dp,
                    animationSpec = tween(durationMillis = 300)
                )

                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticlesScreen(modifier: Modifier = Modifier, viewModel: HomeViewModel = viewModel()) {
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
            viewModel.refresh()
        },
        onLoadMore = {
            viewModel.loadMore()
        },
        hasMore = hasMore,
        listState = listState

    )
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.errorMessage.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

    }
    // ✅ 监听收藏事件
    LaunchedEffect(Unit) {
        viewModel.collectEvent.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White)
            .clickable {
                WebViewActivity.start(context, article.title, article.link)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 12.dp, end = 12.dp, top = 12.dp)
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
        Spacer(modifier = Modifier.height(4.dp))
        HighlightedText(article.title.orEmpty())
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier
                    .width(0.dp)
                    .weight(1f)
                    .wrapContentHeight()
                    .padding(start = 12.dp, bottom = 12.dp),
                text = article.chapterName.orEmpty(),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            val icon =
                if (article.collect == true) Icons.Default.Star else Icons.Default.StarOutline
            Icon(
                icon,
                contentDescription = "收藏",
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable {
                        viewModel.toggleCollect(article)
                    }
            )
        }

    }
}

@Composable
fun HighlightedText(html: String) {
    val regex = Regex("<em.*?>(.*?)</em>")
    val cleanText = html.replace(Regex("<.*?>"), "")

    val annotated = buildAnnotatedString {
        append(cleanText)

        regex.findAll(html).forEach { match ->
            val word = match.groupValues[1]
            val start = cleanText.indexOf(word)
            if (start >= 0) {
                addStyle(
                    style = SpanStyle(
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    ),
                    start = start,
                    end = start + word.length
                )
            }
        }
    }

    Text(
        annotated, modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 12.dp, end = 12.dp),
        fontSize = 16.sp,
        color = Color.Black
    )
}
