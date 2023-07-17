package com.conduent.nationalhighways.utils


import com.conduent.apollo.security.cryptography.Hashing


object Utility {


    fun getSHA256HashedValue(text: String): String {
        return Hashing.hash(text, Hashing.TYPE_SHA256)
    }


    inline fun <T> ArrayDeque<T>.push(element: T) = addLast(element) // returns Unit

    inline fun <T> ArrayDeque<T>.pop() = removeLastOrNull()
}
