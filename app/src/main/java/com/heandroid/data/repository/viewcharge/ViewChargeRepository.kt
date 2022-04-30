package com.heandroid.data.repository.viewcharge

import com.heandroid.data.remote.ApiService
import javax.inject.Inject

class ViewChargeRepository @Inject constructor(private val apiService: ApiService)  {

    suspend fun tollRates()= apiService.getTollRates()
}
