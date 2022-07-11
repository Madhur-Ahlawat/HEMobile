package com.heandroid.data.error.errorUsecase

import com.heandroid.data.error.Error
import com.heandroid.data.error.mapper.ErrorMapper

class ErrorManager(private val errorMapper: ErrorMapper) : ErrorUseCase {
    override fun getError(errorCode: Int): Error {
        return Error(code = errorCode, description = errorMapper.errorsMap.getValue(errorCode))
    }
}