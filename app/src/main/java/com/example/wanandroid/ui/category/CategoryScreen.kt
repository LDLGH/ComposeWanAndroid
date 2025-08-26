package com.example.wanandroid.ui.category

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanandroid.data.model.Category

@Composable
fun CategoryScreen(modifier: Modifier = Modifier, viewModel: CategoryViewModel = viewModel()) {
    val items by viewModel.uiCategoryListState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val errorMsg by viewModel.errorMessage.collectAsState()
    val listState = rememberLazyListState()
    PullToRefreshBoxScreen(
        modifier = modifier,
        isRefreshing = isRefreshing,
        items = items,
        onRefresh = {
            viewModel.refresh()
        },
        listState = listState
    )
    val context = LocalContext.current
    LaunchedEffect(errorMsg) {
        errorMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.refreshIfNeeded(-1)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshBoxScreen(
    items: List<Category>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
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
                    CategoryItem(item)
                }
            }
        }
    }
}


@Composable
fun CategoryItem(category: Category, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White)
            .clickable {
                CategoryListActivity.start(context, category)
            }
    ) {
        Column(
            modifier = Modifier
                .width(0.dp)
                .weight(1f)
                .wrapContentHeight()
                .padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
        ) {
            Text(text = category.name.orEmpty(), fontSize = 16.sp, color = Color.Black)
            Spacer(Modifier.height(8.dp))
            FlowRow(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                category.children?.forEach {
                    Text(text = it.name.orEmpty(), fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = "author",
            Modifier
                .padding(start = 4.dp, end = 10.dp)
                .align(Alignment.CenterVertically)
        )
    }
}