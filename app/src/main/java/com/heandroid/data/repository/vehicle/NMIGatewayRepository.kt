package com.heandroid.data.repository.vehicle

import com.heandroid.data.remote.ApiService
import com.heandroid.utils.common.Constants
import javax.inject.Inject
import javax.inject.Named

class NMIGatewayRepository@Inject  constructor(
    @Named(Constants.NMI)
    private val nmiService: ApiService) {
}