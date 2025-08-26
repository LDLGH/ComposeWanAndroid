package com.example.wanandroid.http

import com.example.wanandroid.data.model.ApiResponse
import com.example.wanandroid.data.model.Articles
import com.example.wanandroid.data.model.Banner
import com.example.wanandroid.data.model.Category
import com.example.wanandroid.data.model.Hotkey
import com.example.wanandroid.data.model.User
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("banner/json")
    suspend fun getBanners(): ApiResponse<List<Banner>>

    @GET("article/list/{page}/json")
    suspend fun getArticles(@Path("page") page: Int): ApiResponse<Articles>

    @GET("tree/json")
    suspend fun getCategory(): ApiResponse<List<Category>>

    @GET("article/list/{page}/json")
    suspend fun getCategoryArticles(
        @Path("page") page: Int,
        @Query("cid") id: Int
    ): ApiResponse<Articles>

    @FormUrlEncoded
    @POST("article/query/0/json")
    suspend fun searchArticles(@Field("k") key: String): ApiResponse<Articles>

    @GET("hotkey/json")
    suspend fun getHotkey(): ApiResponse<List<Hotkey>>

    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): ApiResponse<User?>

    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String,
    ): ApiResponse<User?>

    @GET("lg/collect/list/{page}/json")
    suspend fun getCollectList(@Path("page") page: Int): ApiResponse<Articles>

    // 收藏文章
    @POST("lg/collect/{id}/json")
    suspend fun collectArticle(@Path("id") id: Int): ApiResponse<Any>

    // 取消收藏（从文章列表）
    @POST("lg/uncollect_originId/{id}/json")
    suspend fun unCollectArticle(@Path("id") id: Int): ApiResponse<Any>

    // （可选）取消收藏（我的收藏页面）
    @FormUrlEncoded
    @POST("lg/uncollect/{id}/json")
    suspend fun unCollectFromMyCollect(
        @Path("id") id: Int,
        @Field("originId") originId: Int = -1
    ): ApiResponse<Any>

    @GET("project/tree/json")
    suspend fun getProjectCategory(): ApiResponse<List<Category>>

    @GET("project/list/{page}/json")
    suspend fun getProjectList(
        @Path("page") page: Int,
        @Query("cid") id: Int
    ): ApiResponse<Articles>

}