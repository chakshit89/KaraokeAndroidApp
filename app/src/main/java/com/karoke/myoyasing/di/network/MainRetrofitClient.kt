package com.karoke.myoyasing.di.network

import android.util.Log
import com.karoke.myoyasing.data.remote.LoginApiService
import com.karoke.myoyasing.presentation.navigation.routes.AppDestinationRoutes
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import kotlin.jvm.java

object MainRetrofitClient {

    const val BASE_URL = "http://163.223.186.213:2121/"

    val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("API_LOG", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)

        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpProvider.client)
        .build()


    fun Loginapi(): LoginApiService = retrofit.create(LoginApiService::class.java)

    fun getAllSongs(): LoginApiService = retrofit.create(LoginApiService::class.java)

    fun getSongDetails(): LoginApiService = retrofit.create(LoginApiService::class.java)

    fun UploadSongs(): LoginApiService = retrofit.create(LoginApiService::class.java)

    fun createPerformance(): LoginApiService = retrofit.create(LoginApiService::class.java)

    fun calculateScore(): LoginApiService=retrofit.create(LoginApiService::class.java)

    fun getHashtags(): LoginApiService=retrofit.create(LoginApiService::class.java)

    fun publishPerformances(): LoginApiService=retrofit.create(LoginApiService::class.java)

    fun getFeeds(): LoginApiService=retrofit.create(LoginApiService::class.java)

    fun sharePerformances(): LoginApiService=retrofit.create(LoginApiService::class.java)

    fun LikePerformances(): LoginApiService=retrofit.create(LoginApiService::class.java)

    fun DislikePerformances(): LoginApiService=retrofit.create(LoginApiService::class.java)

    fun getPerformanceComments(): LoginApiService=retrofit.create(LoginApiService::class.java)

    fun AddPerformanceComments(): LoginApiService=retrofit.create(LoginApiService::class.java)

    fun DeletePerformanceComments(): LoginApiService=retrofit.create(LoginApiService::class.java)

    fun FollowUser(): LoginApiService=retrofit.create(LoginApiService::class.java)

    fun UnfollowUser(): LoginApiService=retrofit.create(LoginApiService::class.java)

}