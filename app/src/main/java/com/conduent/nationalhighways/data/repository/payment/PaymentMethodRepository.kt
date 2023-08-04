package com.conduent.nationalhighways.data.repository.payment

import com.conduent.nationalhighways.data.model.payment.AddCardModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodEditModel
import com.conduent.nationalhighways.data.model.payment.SaveNewCardRequest
import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject

class PaymentMethodRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun savedCard() = apiService.savedCard()
    suspend fun deleteCard(model : PaymentMethodDeleteModel?) = apiService.deleteCard(model=model)
    suspend fun editDefaultCard(model : PaymentMethodEditModel?) = apiService.editDefaultCard(model=model)
    suspend fun saveNewCard(model: AddCardModel?) = apiService.savedNewCard(model=model)

    suspend fun saveDirectDebitNewCard(model: SaveNewCardRequest?) = apiService.saveDirectDebitNewCard(model=model)

    suspend fun deletePrimaryCard()=apiService.deletePrimaryCard()

    suspend fun accountDetail() = apiService.accountDetail()



}