package com.heandroid.listener

import com.heandroid.model.DateRangeModel

interface CrossingHistoryFilterDialogListener {
   fun onApplyBtnClicked(dataModel: DateRangeModel)
   fun onCancelBtnClicked()
}