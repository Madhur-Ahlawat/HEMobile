package com.heandroid.data.repository.payment

import com.heandroid.data.model.payment.AddCardModel
import com.heandroid.data.model.payment.PaymentMethodDeleteModel
import com.heandroid.data.model.payment.PaymentMethodEditModel
import com.heandroid.data.remote.ApiService
import javax.inject.Inject

class PaymentMethodRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun savedCard() = apiService.savedCard()
    suspend fun deleteCard(model : PaymentMethodDeleteModel?) = apiService.deleteCard(model=model)
    suspend fun editDefaultCard(model : PaymentMethodEditModel?) = apiService.editDefaultCard(model=model)
    suspend fun saveNewCard(model: AddCardModel?) = apiService.savedNewCard(model=model)
    suspend fun accountDetail() = apiService.accountDetail()



}