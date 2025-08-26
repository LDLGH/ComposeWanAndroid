package com.example.wanandroid.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

@Composable
fun SlidingIndicator(
    pagerState: PagerState,
    count: Int,
    modifier: Modifier = Modifier,
    dotSize: Dp = 6.dp,
    dotSpacing: Dp = 4.dp,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.secondary
) {
    val currentPage = pagerState.currentPage
    val pageOffset = pagerState.currentPageOffsetFraction

    Box(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(dotSpacing)) {
            repeat(count) {
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(inactiveColor)
                )
            }
        }

        // 滑动圆点
        val indicatorWidth = dotSize
        val totalSpacing = dotSpacing + dotSize
        val offset by animateDpAsState(
            targetValue = ((currentPage % count) + pageOffset) * totalSpacing
        )

        Box(
            modifier = Modifier
                .offset(x = offset)
                .size(dotSize)
                .clip(CircleShape)
                .background(activeColor)
        )
    }
}