package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.listener

import com.conduent.nationalhighways.data.model.raiseEnquiry.ServiceRequest
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse

interface ItemClickListener {
    fun onItemClick(details: ServiceRequest?, pos: Int)

}