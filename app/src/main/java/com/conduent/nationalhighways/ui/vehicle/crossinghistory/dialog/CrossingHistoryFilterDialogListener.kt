package com.conduent.nationalhighways.ui.vehicle.crossinghistory.dialog

import com.conduent.nationalhighways.data.model.vehicle.DateRangeModel


interface CrossingHistoryFilterDialogListener {
   fun onRangedApplied(dataModel: DateRangeModel?)
   fun onClearRange(dataModel: DateRangeModel?)
}