package me.naoti.panelapp.network

import android.annotation.SuppressLint
import android.content.Context
import me.naoti.panelapp.R
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

class CookieSenderInterceptor(private val context: Context) : Interceptor {
    companion object {
        const val COOKIE_KEY = "naotimes_app_cookies"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val preferences = context
            .getSharedPreferences(
                context.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
            .getStringSet(COOKIE_KEY, HashSet<String>()) as HashSet<String>
        preferences.forEach {
            builder.addHeader("Cookie", it)
        }

        return chain.proceed(builder.build())
    }
}

class CookieReceiveInterceptor(private val context: Context) : Interceptor {
    companion object {
        const val SET_COOKIE = "Set-Cookie"
        const val COOKIE_KEY = "naotimes_app_cookies"
    }

    @SuppressLint("MutatingSharedPrefs")
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val cookieHeader = originalResponse.headers(SET_COOKIE)
        if (cookieHeader.isNotEmpty()) {
            val cookies = context
                .getSharedPreferences(
                    context.getString(R.string.app_name),
                    Context.MODE_PRIVATE
                )
                .getStringSet(COOKIE_KEY, HashSet<String>()) as HashSet<String>

            cookieHeader.forEach {
                cookies.add(it)
            }

            context
                .getSharedPreferences(
                    context.getString(R.string.app_name),
                    Context.MODE_PRIVATE
                )
                .edit()
                .putStringSet(COOKIE_KEY, cookies)
                .apply()
        }

        return originalResponse
    }
}

fun OkHttpClient.Builder.setCookieStore(context: Context) : OkHttpClient.Builder {
    return this
        .addInterceptor(CookieSenderInterceptor(context))
        .addInterceptor(CookieReceiveInterceptor(context))
}