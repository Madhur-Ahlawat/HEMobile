package com.heandroid.data.error.errorUsecase

import com.heandroid.data.error.Error


interface ErrorUseCase {
    fun getError(errorCode: Int): Error
}
