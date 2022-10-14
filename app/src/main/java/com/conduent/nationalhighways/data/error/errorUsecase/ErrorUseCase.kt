package com.conduent.nationalhighways.data.error.errorUsecase

import com.conduent.nationalhighways.data.error.Error

interface ErrorUseCase {
    fun getError(errorCode: Int): Error
}
