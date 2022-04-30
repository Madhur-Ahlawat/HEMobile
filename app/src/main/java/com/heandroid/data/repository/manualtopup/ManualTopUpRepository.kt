package com.heandroid.data.repository.manualtopup

import com.heandroid.data.model.manualtopup.PaymentWithExistingCardModel
import com.heandroid.data.model.manualtopup.PaymentWithNewCardModel
import com.heandroid.data.remote.ApiService
import javax.inject.Inject

class ManualTopUpRepository  @Inject constructor(private val apiService: ApiService) {
    suspend fun paymentWithNewCard(model: PaymentWithNewCardModel?)= apiService.paymentWithNewCard(model=model)
    suspend fun paymentWithExistingCard(model: PaymentWithExistingCardModel?)= apiService.paymentWithExistingCard(model=model)
}