package com.conduent.nationalhighways.data.repository.manualtopup

import com.conduent.nationalhighways.data.model.manualtopup.PaymentWithExistingCardModel
import com.conduent.nationalhighways.data.model.manualtopup.PaymentWithNewCardModel
import com.conduent.nationalhighways.data.model.revalidate.RevalidateCardModel
import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject

class ManualTopUpRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun paymentWithNewCard(model: PaymentWithNewCardModel?) =
        apiService.paymentWithNewCard(model = model)

    suspend fun paymentWithExistingCard(model: PaymentWithExistingCardModel?) =
        apiService.paymentWithExistingCard(model = model)

    suspend fun paymentWithExistingCard(model: RevalidateCardModel?) =
        apiService.paymentWithExistingCard(model = model)
}