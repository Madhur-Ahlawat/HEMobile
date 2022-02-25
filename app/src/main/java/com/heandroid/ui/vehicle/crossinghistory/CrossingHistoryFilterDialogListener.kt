package com.heandroid.ui.vehicle.crossinghistory

import com.heandroid.data.model.vehicle.DateRangeModel


interface CrossingHistoryFilterDialogListener {
   fun onRangedApplied(dataModel: DateRangeModel?)
   fun onClearRange(dataModel: DateRangeModel?)
}