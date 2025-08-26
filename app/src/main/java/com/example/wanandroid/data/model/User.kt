package com.example.wanandroid.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val admin: Boolean?,
    val chapterTops: List<String>?,
    val coinCount: Int?,
    val collectIds: List<String>?,
    val email: String?,
    val icon: String?,
    val id: Int?,
    val nickname: String?,
    val password: String?,
    val publicName: String?,
    val token: String?,
    val type: Int?,
    val username: String?
)