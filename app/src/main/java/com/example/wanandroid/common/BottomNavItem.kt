package com.example.wanandroid.common

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.wanandroid.R

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    @param:StringRes val label: Int
) {
    object Home : BottomNavItem("home", Icons.Default.Home, R.string.home)
    object Category : BottomNavItem("category", Icons.AutoMirrored.Filled.List, R.string.category)
    object Project : BottomNavItem("project", Icons.Default.Class, R.string.project)
}