package com.heandroid.ui.account.creation.findyourvehicle

import com.heandroid.data.remote.ApiService
import com.heandroid.utils.common.Constants
import javax.inject.Inject

class FindYourVehicleRepo @Inject constructor(private val apiService: ApiService) {

    suspend fun getVehicleDetail(vehicleNumber: String?, agencyId: Int?) =
        apiService.getAccountFindVehicle(vehicleNumber, agencyId)
}