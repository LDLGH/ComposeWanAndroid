package com.example.wanandroid.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object EventBus {

    private val _events = MutableSharedFlow<Any>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events = _events.asSharedFlow()

    val stickyEvents = mutableMapOf<Class<*>, Any>()
    val mutex = Mutex()

    suspend fun post(event: Any) {
        _events.emit(event)
    }

    suspend fun postSticky(event: Any) {
        mutex.withLock {
            stickyEvents[event::class.java] = event
        }
        _events.emit(event)
    }

    inline fun <reified T> subscribe(): Flow<T> {
        return events.filter { it is T }.map { it as T }
    }

    inline fun <reified T> subscribeSticky(): Flow<T> = flow {
        mutex.withLock {
            stickyEvents[T::class.java]?.let { emit(it as T) }
        }
        emitAll(subscribe<T>())
    }
}

/**
 * 在 Composable 中订阅普通事件
 */
@Composable
inline fun <reified T> CollectEvent(
    crossinline onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        EventBus.subscribe<T>()
            .flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { onEvent(it) }
    }
}

/**
 * 在 Composable 中订阅粘性事件
 */
@Composable
inline fun <reified T> CollectStickyEvent(
    crossinline onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        EventBus.subscribeSticky<T>()
            .flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { onEvent(it) }
    }
}
