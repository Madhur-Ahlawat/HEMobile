package com.conduent.nationalhighways.data.error.mapper

import android.content.Context
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.error.CHECK_YOUR_FIELDS
import com.conduent.nationalhighways.data.error.CREATE_ACCOUNT_FAILED
import com.conduent.nationalhighways.data.error.NETWORK_ERROR
import com.conduent.nationalhighways.data.error.NO_INTERNET_CONNECTION
import com.conduent.nationalhighways.data.error.PASS_WORD_ERROR
import com.conduent.nationalhighways.data.error.SERVER_ERROR
import com.conduent.nationalhighways.data.error.TOO_MANY_LOGIN_ATTEMPT
import com.conduent.nationalhighways.data.error.UNKNOWN_ERROR
import com.conduent.nationalhighways.data.error.USER_NAME_ERROR

class ErrorMapper(val context: Context) : ErrorMapperSource {

    override fun getErrorString(errorId: Int): String {
        return context.getString(errorId)
    }

    override val errorsMap: Map<Int, String>
        get() = mapOf(
            Pair(NO_INTERNET_CONNECTION, getErrorString(R.string.no_internet)),
            Pair(NETWORK_ERROR, getErrorString(R.string.network_error)),
            Pair(PASS_WORD_ERROR, getErrorString(R.string.invalid_password)),
            Pair(USER_NAME_ERROR, getErrorString(R.string.invalid_username)),
            Pair(CHECK_YOUR_FIELDS, getErrorString(R.string.invalid_username_and_password)),
            Pair(UNKNOWN_ERROR, getErrorString(R.string.unknown_error)),
            Pair(TOO_MANY_LOGIN_ATTEMPT, getErrorString(R.string.too_many_login_attempt)),
            Pair(CREATE_ACCOUNT_FAILED, getErrorString(R.string.cannot_create_account)),

            Pair(SERVER_ERROR, getErrorString(R.string.internal_server_error))

        ).withDefault { getErrorString(R.string.network_error) }
}
