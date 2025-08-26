package com.example.wanandroid.data.model

import java.io.Serializable

data class Category(
    val articleList: List<Any>?,
    val author: String?,
    val children: List<CategoryChildren>?,
    val courseId: Int?,
    val cover: String?,
    val desc: String?,
    val id: Int?,
    val lisense: String?,
    val lisenseLink: String?,
    val name: String?,
    val order: Int?,
    val parentChapterId: Int?,
    val type: Int?,
    val userControlSetTop: Boolean?,
    val visible: Int?
) : Serializable

data class CategoryChildren(
    val articleList: List<Any>?,
    val author: String?,
    val children: List<Any>?,
    val courseId: Int?,
    val cover: String?,
    val desc: String?,
    val id: Int?,
    val lisense: String?,
    val lisenseLink: String?,
    val name: String?,
    val order: Int?,
    val parentChapterId: Int?,
    val type: Int?,
    val userControlSetTop: Boolean?,
    val visible: Int?
) : Serializable