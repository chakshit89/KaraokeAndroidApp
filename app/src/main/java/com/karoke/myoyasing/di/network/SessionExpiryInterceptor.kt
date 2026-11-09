package com.karoke.myoyasing.di.network

import okhttp3.Interceptor
import okhttp3.Response

class SessionExpiryInterceptor : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val response = chain.proceed(chain.request())

        if (response.code == 403) {
            SessionManager.notifySessionExpired()
        }

        return response
    }
}