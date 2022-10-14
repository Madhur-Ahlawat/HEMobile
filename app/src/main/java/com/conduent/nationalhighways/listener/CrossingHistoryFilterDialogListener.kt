package com.conduent.nationalhighways.listener

import com.conduent.nationalhighways.data.model.vehicle.DateRangeModel

interface CrossingHistoryFilterDialogListener {
   fun onRangedApplied(dataModel: DateRangeModel?)
   fun onClearRange(dataModel: DateRangeModel?)
}