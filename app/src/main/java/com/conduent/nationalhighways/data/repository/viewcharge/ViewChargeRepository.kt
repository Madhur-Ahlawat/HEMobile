package com.conduent.nationalhighways.data.repository.viewcharge

import com.conduent.nationalhighways.data.remote.ApiService
import javax.inject.Inject

class ViewChargeRepository @Inject constructor(private val apiService: ApiService)  {

    suspend fun tollRates()= apiService.getTollRates()
}
