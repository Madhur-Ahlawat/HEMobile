package com.heandroid.data.remote

import android.content.Context
import android.content.Intent
import com.heandroid.ui.landing.LandingActivity
import com.heandroid.utils.common.Constants
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val context: Context
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            if (response.request.header(HEADER_AUTHORIZATION) != null ||
                response.code == TOKEN_FAIL
            ) {
                logoutUser()
            } else {
                return null
            }
        }
        return null
    }

    private fun logoutUser() {
        context.startActivity(
            Intent(context, LandingActivity::class.java)
                .putExtra(Constants.SHOW_SCREEN, Constants.FAILED_RETRY_SCREEN)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val TOKEN_FAIL = 401
    }

}
