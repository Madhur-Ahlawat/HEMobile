package com.heandroid.utils.common

import android.util.Log

object Logg {

    public fun logging(tag: String, message: String) {
        Log.v(tag, message)
    }
}