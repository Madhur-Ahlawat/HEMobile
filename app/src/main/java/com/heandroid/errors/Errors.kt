package com.heandroid.errors

const val GENERIC_ERROR = "Unknown Error"

open class ApplicationException(val errorTitle: String,
                                val errorDescription: String?) : RuntimeException()

class ServerCommunicationException(
    errorTitle: String,
    errorDescription: String?) : ApplicationException(errorTitle, errorDescription)

class InvalidIdTokenException(errorDescription: String) :
    ApplicationException("Invalid ID Token", errorDescription)