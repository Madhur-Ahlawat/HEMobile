package com.heandroid.ui.vehicle.crossinghistory.dialog

import com.heandroid.data.model.vehicle.DateRangeModel


interface CrossingHistoryFilterDialogListener {
   fun onRangedApplied(dataModel: DateRangeModel?)
   fun onClearRange(dataModel: DateRangeModel?)
}