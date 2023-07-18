package com.conduent.nationalhighways.utils


import com.conduent.apollo.security.cryptography.Hashing
import java.lang.Exception


object Utility {


    fun getSHA256HashedValue(text: String): String {
        return Hashing.hash(text, Hashing.TYPE_SHA256)
    }

    fun isNumber(s: String?): Boolean {
        try{
            return if (s.isNullOrEmpty()) false else s.all { Character.isDigit(it) }
        }
        catch(e:Exception){
            return false
        }
    }
    inline fun <T> ArrayDeque<T>.push(element: T) = addLast(element) // returns Unit

    inline fun <T> ArrayDeque<T>.pop() = removeLastOrNull()
}
