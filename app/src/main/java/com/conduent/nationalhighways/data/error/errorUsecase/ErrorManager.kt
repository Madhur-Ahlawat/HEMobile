package com.conduent.nationalhighways.data.error.errorUsecase

import com.conduent.nationalhighways.data.error.Error
import com.conduent.nationalhighways.data.error.mapper.ErrorMapper

class ErrorManager(private val errorMapper: ErrorMapper) : ErrorUseCase {
    override fun getError(errorCode: Int): Error {
        return Error(code = errorCode, description = errorMapper.errorsMap.getValue(errorCode))
    }
}