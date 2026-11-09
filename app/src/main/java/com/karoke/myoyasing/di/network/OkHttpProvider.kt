package com.karoke.myoyasing.di.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkHttpProvider {

    private val loggingInterceptor =
        HttpLoggingInterceptor { message ->
            Log.d("API_LOG", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY

            redactHeader("Authorization")
        }

    val client: OkHttpClient by lazy {

        OkHttpClient.Builder()

            .addInterceptor(AuthInterceptor())
            .addInterceptor(SessionExpiryInterceptor())
            .addInterceptor(loggingInterceptor)

            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .callTimeout(120, TimeUnit.SECONDS)

            .build()
    }
}