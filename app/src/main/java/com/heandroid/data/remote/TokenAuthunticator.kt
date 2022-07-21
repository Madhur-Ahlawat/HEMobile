package com.heandroid.data.remote

import android.content.Context
import android.util.Log
import com.heandroid.utils.common.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Provider

class TokenAuthImpl(
    @param:ApplicationContext private val context: Context,
    private val sessionManager: SessionManager,
    val api: Provider<ApiService>
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        Log.i("teja1234", "1")
        val token = sessionManager.fetchAuthToken() ?: return null
        synchronized(this) {
            Log.i("teja1234", "2")

            val newToken = sessionManager.fetchAuthToken() ?: return null

            if (response.request.header(HEADER_AUTHORIZATION) != null) {
                Log.i("teja1234", "3")
                if (newToken != token) {
                    Log.i("teja1234", "3-1")
                    return response.request
                        .newBuilder()
                        .removeHeader(HEADER_AUTHORIZATION)
                        .addHeader(
                            HEADER_AUTHORIZATION,
                            "Bearer $newToken"
                        ).build()
                }
                sessionManager.fetchRefreshToken()?.let { refresh ->
                    Log.i("teja1234", "4")
                    try {
                        val tokenResponse = runBlocking {
                            Log.i("teja1234", "5")
                            api.get().refreshToken(
                                refresh_token = refresh
                            )
                        }
                        Log.i("teja1234", "5---")
                        if (tokenResponse?.isSuccessful == true) {
                            Log.i("teja1234", "6")
                            val (_, accessToken, _, refreshToken) = tokenResponse.body()
                                ?: return null
                            sessionManager.saveAuthToken(accessToken)
                            if (refreshToken != null) {
                                Log.i("teja1234", "1")
                                sessionManager.saveRefreshToken(refreshToken)
                            }

                            return response.request
                                .newBuilder()
                                .removeHeader(HEADER_AUTHORIZATION)
                                .addHeader(
                                    HEADER_AUTHORIZATION,
                                    "Bearer $accessToken"
                                ).build()
                        } else {
                            Log.i("teja1234", "7")
                            if (tokenResponse?.code() == REFRESH_TOKEN_FAIL) {
                                logoutUser()
                            } else {
                                // todo
                                logoutUser()
                            }
                        }

                    } catch (e: Exception) {
                        return null
                    }
                }
            } else {
                return null
            }
        }
        return null
    }

    private fun logoutUser() {

    }

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val REFRESH_TOKEN_FAIL = 403
    }

}
