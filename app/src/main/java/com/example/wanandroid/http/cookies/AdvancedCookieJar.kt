package com.example.wanandroid.http.cookies

import android.content.Context
import androidx.core.content.edit
import com.example.wanandroid.app.App
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

class AdvancedCookieJar() : CookieJar {

    private val prefs = App.instance.getSharedPreferences("cookies_prefs", Context.MODE_PRIVATE)
    private val cookieStore =
        ConcurrentHashMap<String, MutableList<Cookie>>() // host -> cookie list

    init {
        // 读取 SharedPreferences 的 Cookie
        prefs.all.forEach { entry ->
            val cookieStrings = (entry.value as? String)?.split(";") ?: emptyList()
            val cookies = cookieStrings.mapNotNull {
                Cookie.parse(
                    HttpUrl.Builder().scheme("https").host(entry.key).build(), it
                )
            }
            if (cookies.isNotEmpty()) {
                cookieStore[entry.key] = cookies.toMutableList()
            }
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        val list = cookieStore[host] ?: mutableListOf()
        cookies.forEach { newCookie ->
            list.removeAll { it.name == newCookie.name && it.path == newCookie.path } // 覆盖旧的
            if (!newCookie.hasExpired()) {
                list.add(newCookie)
            }
        }
        cookieStore[host] = list

        // 保存到 SharedPreferences
        val cookieString = list.joinToString(";") { it.toString() }
        prefs.edit { putString(host, cookieString) }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host
        val cookies = cookieStore[host]?.filter { !it.hasExpired() } ?: emptyList()
        return cookies.filter { url.encodedPath.startsWith(it.path) } // path 匹配
    }

    fun clear() {
        cookieStore.clear()
        prefs.edit { clear() }
    }

    private fun Cookie.hasExpired(): Boolean {
        return expiresAt < System.currentTimeMillis()
    }
}
