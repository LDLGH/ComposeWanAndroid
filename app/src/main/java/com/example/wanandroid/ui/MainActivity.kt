package com.example.wanandroid.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Output
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.wanandroid.R
import com.example.wanandroid.common.BottomNavItem
import com.example.wanandroid.ui.category.CategoryScreen
import com.example.wanandroid.ui.collect.CollectListActivity
import com.example.wanandroid.ui.home.HomeScreen
import com.example.wanandroid.ui.login.LoginActivity
import com.example.wanandroid.ui.login.LoginViewModel
import com.example.wanandroid.ui.project.ProjectScreen
import com.example.wanandroid.ui.search.SearchActivity
import com.example.wanandroid.ui.theme.WanAndroidTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Category,
        BottomNavItem.Project
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WanAndroidTheme {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            ModalDrawerSheetScreen()
                        }
                    }) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    // 根据 route 映射标题
                    val title = when (currentRoute) {
                        BottomNavItem.Home.route -> stringResource(id = R.string.app_name)
                        BottomNavItem.Category.route -> stringResource(id = R.string.category)
                        BottomNavItem.Project.route -> stringResource(id = R.string.project)
                        else -> "WanAndroid"
                    }
                    val context = LocalContext.current
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                colors = topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.primary,
                                ),
                                title = {
                                    Text(title)
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            if (drawerState.isClosed) {
                                                drawerState.open()
                                            } else {
                                                drawerState.close()
                                            }
                                        }
                                    }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                                    }
                                },
                                actions = {
                                    IconButton(onClick = {
                                        SearchActivity.start(context)
                                    }) {
                                        Icon(Icons.Default.Search, contentDescription = "Search")
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            NavigationBar {
                                bottomNavItems.forEach { item ->
                                    NavigationBarItem(
                                        selected = currentRoute == item.route,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                // 清理栈
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                // 避免重复创建页面，类似 Fragment 的 singleTop
                                                launchSingleTop = true
                                                // 回退时保存状态
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                item.icon,
                                                contentDescription = stringResource(item.label)
                                            )
                                        },
                                        label = { Text(stringResource(item.label)) }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController,
                            startDestination = BottomNavItem.Home.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(BottomNavItem.Home.route) { HomeScreen() }
                            composable(BottomNavItem.Category.route) { CategoryScreen() }
                            composable(BottomNavItem.Project.route) { ProjectScreen() }
                        }
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WanAndroidTheme {
    }
}

@Composable
fun ModalDrawerSheetScreen(viewModel: LoginViewModel = viewModel()) {
    val user = viewModel.userFlow.collectAsState().value
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Image(
                painter = painterResource(id = R.mipmap.logo),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(12.dp))
            if (user != null) {
                Text(
                    text = user.username.orEmpty(),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.titleMedium,
                )
            } else {
                Text(
                    text = stringResource(id = R.string.not_logged_in),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            LoginActivity.start(context)
                        },
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(12.dp))
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.my_like)) },
            selected = false,
            icon = { Icon(Icons.Outlined.Star, contentDescription = null) },
            onClick = {
                if (user == null) {
                    LoginActivity.start(context)
                } else {
                    CollectListActivity.start(context)
                }
            }
        )
        if (user != null) {
            NavigationDrawerItem(
                label = { Text(stringResource(R.string.logout)) },
                selected = false,
                icon = { Icon(Icons.Outlined.Output, contentDescription = null) },
                onClick = { viewModel.logout() }
            )
        }
    }
}



