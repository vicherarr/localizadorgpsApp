package com.vicherarr.locgps.data.session

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
            .header("Accept", "application/json")

        val token = tokenProvider()?.takeIf { it.isNotBlank() }
        if (token != null && !original.url.encodedPath.contains("/api/autenticacion/inicio-sesion")) {
            builder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(builder.build())
    }
}
