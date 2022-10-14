package com.conduent.nationalhighways.utils.common

import android.util.Log

object Logg {

    fun logging(tag: String, message: String) {
        Log.v(tag, message)
    }
}