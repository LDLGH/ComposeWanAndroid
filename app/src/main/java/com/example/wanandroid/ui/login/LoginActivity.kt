package com.example.wanandroid.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanandroid.R
import com.example.wanandroid.common.EventBus
import com.example.wanandroid.common.UserPreferences
import com.example.wanandroid.data.event.LoginEvent
import com.example.wanandroid.ui.theme.WanAndroidTheme
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, LoginActivity::class.java)
            context.startActivity(starter)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WanAndroidTheme {
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
                                    stringResource(R.string.login),
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
                            }
                        )
                    },
                ) { innerPadding ->
                    LoginRegisterContainer(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginRegisterContainer(modifier: Modifier = Modifier, viewModel: LoginViewModel = viewModel()) {
    var isLogin by remember { mutableStateOf(true) } // true = Login, false = Register

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = isLogin,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            }
        ) { targetLogin ->
            if (targetLogin) {
                LoginScreen(
                    viewModel = viewModel,
                    onSwitch = { isLogin = false }
                )
            } else {
                RegisterScreen(
                    viewModel = viewModel,
                    onSwitch = { isLogin = true }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
    onSwitch: () -> Unit
) {
    val isLoading by viewModel.uiLoginState.collectAsState()
    val user by viewModel.uiLoginData.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    // 监听错误消息
    LaunchedEffect(Unit) {
        viewModel.errorMessage.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
    // 监听登录成功
    LaunchedEffect(user) {
        user?.let {
            UserPreferences.saveUser(context, it)
            EventBus.post(LoginEvent(true))
            Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show()
            (context as? ComponentActivity)?.finish()
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = modifier.fillMaxSize()) {
            var userName by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var userNameError by remember { mutableStateOf(false) }
            var passwordError by remember { mutableStateOf(false) }
            val isLoginEnabled = userName.isNotBlank() && password.length >= 6

            Spacer(modifier = Modifier.height(60.dp))
            OutlinedTextField(
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxWidth(),
                value = userName,
                onValueChange = {
                    userName = it
                    userNameError = it.isBlank()
                },
                label = { Text(text = stringResource(R.string.username)) },
                singleLine = true,
                isError = userNameError
            )
            if (userNameError) {
                Text(
                    text = "用户名不能为空",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 30.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            PasswordTextField(
                password = password,
                onPasswordChange = {
                    password = it
                    passwordError = it.length < 6
                },
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxWidth(),
                label = stringResource(R.string.password),
                isError = passwordError
            )
            if (passwordError) {
                Text(
                    text = "密码至少 6 位",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 30.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxWidth(),
                onClick = {
                    scope.launch {
                        if (isLoginEnabled) {
                            viewModel.login(userName, password)
                        } else {
                            Toast.makeText(context, "用户名或密码错误", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                },
                enabled = isLoginEnabled
            ) {
                Text(text = stringResource(R.string.login))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "没有账号？去注册",
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 30.dp)
                    .clickable {
                        onSwitch()
                    },
                color = Color.Gray
            )
        }
        // 👇 请求加载菊花 (Overlay 居中)
        if (isLoading) {
            Dialog(onDismissRequest = { /* 不允许点击外部取消 */ }) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.password),
    isError: Boolean = false,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        modifier = modifier,
        label = { Text(text = label) },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff
            val description = if (passwordVisible) "隐藏密码" else "显示密码"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = description)
            }
        },
        isError = isError
    )
}

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(), // 也可以单独建 RegisterViewModel
    onSwitch: () -> Unit
) {
    val isLoading by viewModel.uiRegisterState.collectAsState() // 注册加载状态
    val user by viewModel.uiLoginData.collectAsState() // 注册成功后返回的用户信息
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = (context as? ComponentActivity)

    // 监听错误消息
    LaunchedEffect(Unit) {
        viewModel.errorMessage.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // 监听注册成功
    LaunchedEffect(user) {
        user?.let {
            UserPreferences.saveUser(context, it)
            EventBus.post(LoginEvent(true))
            Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show()
            activity?.finish() // 关闭注册 Activity
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        var userName by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        var userNameError by remember { mutableStateOf(false) }
        var passwordError by remember { mutableStateOf(false) }
        var confirmPasswordError by remember { mutableStateOf(false) }

        val isRegisterEnabled =
            userName.isNotBlank() &&
                    password.length >= 6 &&
                    confirmPassword == password

        Spacer(modifier = Modifier.height(60.dp))

        // 用户名
        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxWidth(),
            value = userName,
            onValueChange = {
                userName = it
                userNameError = it.isBlank()
            },
            label = { Text(text = stringResource(R.string.username)) },
            singleLine = true,
            isError = userNameError
        )
        if (userNameError) {
            Text(
                text = "用户名不能为空",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 30.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 密码
        PasswordTextField(
            password = password,
            onPasswordChange = {
                password = it
                passwordError = it.length < 6
            },
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxWidth(),
            label = stringResource(R.string.password),
            isError = passwordError
        )
        if (passwordError) {
            Text(
                text = "密码至少 6 位",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 30.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 确认密码
        PasswordTextField(
            password = confirmPassword,
            onPasswordChange = {
                confirmPassword = it
                confirmPasswordError = it != password
            },
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxWidth(),
            label = "确认密码",
            isError = confirmPasswordError
        )
        if (confirmPasswordError) {
            Text(
                text = "两次输入的密码不一致",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 30.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 注册按钮
        Button(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxWidth(),
            onClick = {
                scope.launch {
                    if (isRegisterEnabled) {
                        viewModel.register(userName, password, confirmPassword)
                    } else {
                        Toast.makeText(context, "请检查输入", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            enabled = isRegisterEnabled
        ) {
            Text(text = stringResource(R.string.register))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "已有账号？去登录",
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 30.dp)
                .clickable {
                    onSwitch()
                },
            color = Color.Gray
        )
    }

    // 显示加载对话框
    if (isLoading) {
        AlertDialog(
            onDismissRequest = { /* 禁止手动关闭 */ },
            title = { Text("请稍候") },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("正在注册...")
                }
            },
            confirmButton = {}
        )
    }
}
