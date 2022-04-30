package com.heandroid.listener

import com.heandroid.data.model.vehicle.DateRangeModel

interface CrossingHistoryFilterDialogListener {
   fun onRangedApplied(dataModel: DateRangeModel?)
   fun onClearRange(dataModel: DateRangeModel?)
}