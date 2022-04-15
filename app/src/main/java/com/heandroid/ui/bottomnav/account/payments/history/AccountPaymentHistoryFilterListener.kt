package com.heandroid.ui.bottomnav.account.payments.history

import com.heandroid.data.model.vehicle.VehicleResponse

interface AccountPaymentHistoryFilterListener {
    fun onApplyFilterClick(vehicleName : String?, startDate : String?, endDate : String?)
}