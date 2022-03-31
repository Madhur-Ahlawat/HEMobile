package com.heandroid.ui.bottomnav.account.payments

import com.heandroid.data.model.accountpayment.AccountPaymentHistoryRequest
import com.heandroid.data.model.contactdartcharge.CaseEnquiryHistoryRequest
import com.heandroid.data.remote.ApiService
import javax.inject.Inject

class AccountPaymentHistoryRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun getAccountPayment(requestParam: AccountPaymentHistoryRequest?) =
        apiService.getPaymentHistoryData(requestParam)
}